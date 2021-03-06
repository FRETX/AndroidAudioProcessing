package rocks.fretx.audioprocessing;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Onur Babacan on 27-Oct-16.
 */

public class AudioProcessing {
	private AudioInputHandler handler;
	private Thread audioThread;
	private boolean processingIsRunning = false;
	private boolean initialized = false;
	protected ChordDetector chordDetector;
	protected PitchDetector pitchDetector;
	protected NoteDetector noteDetector;
	protected ArrayList<Chord> targetChords = new ArrayList<Chord>(0);
	protected int frameLength;



	public float getPitch(){
		if(pitchDetector == null) return -1;
		return pitchDetector.medianPitch;
	}

	public Chord getChord(){
		if(chordDetector == null) return new Chord("X","X");
		return chordDetector.detectedChord;
	}

	public double getChordSimilarity(){
		if(chordDetector == null) return -1;
		return chordDetector.getChordSimilarity();
	}

	public int getMidiNote(){
		if(noteDetector == null) return -1;
		return noteDetector.noteMidi;
	}

	public String getNoteName(){
		if(noteDetector == null) return null;
		return noteDetector.noteName;
	}

	public double getVolume(){
		if(handler == null) return -1;
		return handler.getVolume();
	}

	public void initialize(int targetFs, double bufferSizeInSeconds){
		//TODO: take fs as input and give warning if requested fs is unavailable
		//TODO: take other parameters as input optionally
		//TODO: make parameters dynamic, or display their baking clearly in code
//		int maxFs = AudioInputHandler.getMaxSamplingFrequency();
//		int targetFs = maxFs;
//		int targetFs = 8000;
//		double bufferSizeInSeconds = 0.25;

		int minBufferSize = AudioInputHandler.getMinBufferSize(targetFs);

		int targetBufferSize = (int) Math.round(targetFs * bufferSizeInSeconds);
		//round up to nearest power of 2
		int audioBufferSize = (int) Math.pow(2, Math.ceil(Math.log((double) targetBufferSize) / Math.log(2)));

		//TODO: compare to minBufferSize and handle errors

		//TODO: take frameLength as a parameter as well

		frameLength = audioBufferSize / 2;

		handler = new AudioInputHandler(targetFs, audioBufferSize);

//		Log.d("AudioProcessing" , "fs: " + Integer.toString(maxFs) + " frameLength: " + Integer.toString(frameLength));

		float frameOverlap = 0.5f;
		float yinThreshold = 0.10f;
		pitchDetector = new PitchDetector(targetFs, frameLength, frameLength/2, yinThreshold);

		noteDetector = new NoteDetector();

		//TODO: make target chords dynamic
		String[] majorRoots = new String[]{"A", "C", "D", "E", "F", "G"};
		String[] minorRoots = new String[]{"A", "B", "D", "E"};
		for (int i = 0; i < majorRoots.length; i++) {
			targetChords.add(new Chord(majorRoots[i], "maj"));
		}
		for (int i = 0; i < minorRoots.length; i++) {
			targetChords.add(new Chord(minorRoots[i], "m"));
		}

		//Create new chord detector
		chordDetector = new ChordDetector(handler.samplingFrequency, frameLength, frameLength / 4, targetChords);
		//Patch it to audio handler
		handler.addAudioAnalyzer(pitchDetector);
		handler.addAudioAnalyzer(chordDetector);
		//Patch pitch detector to the note detector
		pitchDetector.addParameterAnalyzer(noteDetector);
		initialized = true;

	}

	public void setTargetChords(ArrayList<Chord> chords){
		targetChords.clear();
		for (int i = 0; i < chords.size(); i++) {
			targetChords.add(chords.get(i));
		}
		targetChords.add(new Chord("X","X"));
		Log.d("setting target chords: ", targetChords.toString());
		chordDetector.setTargetChords(targetChords);
	}

	public ArrayList<Chord> getTargetChords(){
		return targetChords;
	}

	public void start(){
		//Start the audio thread
		if(initialized){
			audioThread = new Thread(handler, "Audio Thread");
			audioThread.start();
			processingIsRunning = true;
			Log.d("AudioProcessing.start", "Audio Thread started");
		} else {
			//TODO: handle errors
		}
	}

	public void stop(){
		if (processingIsRunning) {
			if (handler != null) {
				handler.onDestroy();
				handler = null;
			}
			if (audioThread != null) {
				try {
					audioThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				audioThread = null;
			}
			//Also release audioAnalyzers here, if any
			chordDetector = null;
			noteDetector = null;
			pitchDetector = null;
			processingIsRunning = false;
			initialized = false;
			Log.d("AudioProcessing.stop()", "processes stopped");
		}
	}

	public void pause(){

	}

	public boolean isInitialized(){
		if(initialized){
			return true;
		} else {
			return false;
		}
	}

	public boolean isProcessing(){
		if (processingIsRunning) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isBufferAvailable(){
		if(handler.isBufferAvailable()){
			return true;
		} else return false;
	}

	public void enablePitchDetector() {
		pitchDetector.enable();
	}

	public void disablePitchDetector() {
		pitchDetector.disable();
	}

	public void enableNoteDetector() {
		noteDetector.enable();
	}

	public void disableNoteDetector(){
		noteDetector.disable();
	}

	public void disableChordDetector(){
		chordDetector.disable();
	}

	public void enableChordDetector() {
		chordDetector.enable();
	}

}