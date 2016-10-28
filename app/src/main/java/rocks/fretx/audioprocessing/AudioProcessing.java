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
	protected PitchDetectorYin pitchDetector;
	protected NoteDetector noteDetector;
	protected ArrayList<Chord> targetChords = new ArrayList<Chord>(0);


	public float getPitch(){
		return pitchDetector.medianPitch;
	}

	public Chord getChord(){
		return chordDetector.detectedChord;
	}

	public int getMidiNote(){
		return noteDetector.noteMidi;
	}

	public String getNoteName(){
		return noteDetector.noteName;
	}

	public void initialize(){
		//TODO: take fs as input and give warning if requested fs is unavailable
		//TODO: take other parameters as input optionally
//		int maxFs = AudioInputHandler.getMaxSamplingFrequency();
//		int targetFs = maxFs;
		int targetFs = 8000;

		int minBufferSize = AudioInputHandler.getMinBufferSize(targetFs);
		double bufferSizeInSeconds = 0.25;
		int targetBufferSize = (int) Math.round(targetFs * bufferSizeInSeconds);
		int audioBufferSize = (int) Math.pow(2, Math.ceil(Math.log((double) targetBufferSize) / Math.log(2))); //round up to nearest power of 2

		//TODO: compare to minBufferSize and handle errors

		int frameLength = audioBufferSize / 2;

		handler = new AudioInputHandler(targetFs, audioBufferSize);

//		Log.d("AudioProcessing" , "fs: " + Integer.toString(maxFs) + " frameLength: " + Integer.toString(frameLength));

//		int minF0 = 60;
		float frameOverlap = 0.5f;
		float yinThreshold = 0.10f;
		pitchDetector = new PitchDetectorYin(targetFs, frameLength, Math.round((float) frameLength * frameOverlap), yinThreshold);
		//Patch pitch detector to the note detector
		noteDetector = new NoteDetector(pitchDetector);

		//TODO: make target chords dynamic
		String[] majorRoots = new String[]{"A", "C", "D", "E", "F", "G"};
		String[] minorRoots = new String[]{"A", "B", "D", "E"};
		for (int i = 0; i < majorRoots.length; i++) {
			targetChords.add(new Chord(majorRoots[i], "maj"));
		}
		for (int i = 0; i < minorRoots.length; i++) {
			targetChords.add(new Chord(minorRoots[i], "m"));
		}

		//TODO: make parameters dynamic, or display their baking clearly in code
		//Create new chord detector
		chordDetector = new ChordDetector(handler.samplingFrequency, frameLength, frameLength / 4, targetChords);
		//Patch it to audio handler
		handler.addAudioAnalyzer(pitchDetector);
		handler.addAudioAnalyzer(chordDetector);
		handler.addPatchedAnalyzer(noteDetector);
		initialized = true;

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
}