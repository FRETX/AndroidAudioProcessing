package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 27-Oct-16.
 */

public class NoteDetector implements PatchedAnalyzer{

	protected String noteName;
	protected int noteMidi;
	protected double error;
	private PitchDetector pitchDetector;

	public NoteDetector(PitchDetector pitchDetector){
		this.pitchDetector = pitchDetector;
	}

	public void process(){
		float pitch = pitchDetector.medianPitch;
		if (pitch > -1) {
			noteMidi = (int) Math.round(MusicUtils.hzToMidiNote(pitch));
			error = Math.abs((double) noteMidi - MusicUtils.hzToMidiNote(pitch));
			noteName = MusicUtils.midiNoteToName(Math.round((float) noteMidi));
		} else {
			noteMidi = -1;
			error = Double.POSITIVE_INFINITY;
			noteName = null;
		}
	}

	@Override
	public void processingFinished() {

	}

}
