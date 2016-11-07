package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 27-Oct-16.
 */

public class NoteDetector extends ParameterAnalyzer {

	protected String noteName;
	protected int noteMidi;
	protected double error;

	public NoteDetector(){
		super();
	}

	public void internalProcess(double input){
		if (input > -1) {
			noteMidi = (int) Math.round(MusicUtils.hzToMidiNote(input));
			error = Math.abs((double) noteMidi - MusicUtils.hzToMidiNote(input));
			noteName = MusicUtils.midiNoteToName(Math.round((float) noteMidi));
			output = noteMidi;
		} else {
			noteMidi = -1;
			error = Double.POSITIVE_INFINITY;
			noteName = null;
			output = -1;
		}
	}

	@Override
	public void processingFinished() {

	}

}
