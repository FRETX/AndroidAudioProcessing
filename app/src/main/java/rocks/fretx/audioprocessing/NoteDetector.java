package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 27-Oct-16.
 */

public class NoteDetector implements PatchedAnalyzer{

	protected String noteName;
	protected int noteMidi;
	protected double error;
	private PitchDetectorYin pitchDetector;

	public NoteDetector(PitchDetectorYin pitchDetector){
		this.pitchDetector = pitchDetector;
	}

	public void process(){
		float pitch = pitchDetector.medianPitch;
		if (pitch > -1) {
			noteMidi = (int) Math.round(hzToMidiNote(pitch));
			error = Math.abs((double) noteMidi - hzToMidiNote(pitch));
			noteName = midiNoteToName(Math.round((float) noteMidi));
		} else {
			noteMidi = -1;
			error = Double.POSITIVE_INFINITY;
			noteName = null;
		}
	}

	@Override
	public void processingFinished() {

	}

	public static double midiNoteToHz(int midiNote) {
		return Math.pow(2, ((double) midiNote - 69) / 12) * 440;
	}

	public static double hzToMidiNote(double hertz) {

		return 69 + (12 * Math.log(hertz / 440) / Math.log(2));
	}

	public static String midiNoteToName(int midiNote) {
		String[] noteString = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
		int octave = (midiNote / 12) - 1;
		int noteIndex = (midiNote % 12);
		return (noteString[noteIndex] + Integer.toString(octave));
	}

	public static FretboardPosition midiNoteToFretboardPosition(int note) {
		if (note < 40 || note > 68) {
			throw new IllegalArgumentException("This note is outside the display range of FretX");
		}
		if (note > 59) {
			note++;
		}
		int fret = (note - 40) % 5;
		int string = 6 - ((note - 40) / 5);
		//This formula always prefers the open 2nd string to the 4th fret of the 3rd string
		return new FretboardPosition(string, fret);
	}


}
