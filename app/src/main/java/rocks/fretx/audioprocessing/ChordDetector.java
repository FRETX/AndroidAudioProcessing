package rocks.fretx.audioprocessing;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Onur Babacan on 10/20/16.
 */

public class ChordDetector extends AudioAnalyzer {

    private double[] tempBuffer;
    private ArrayList<Chord> targetChords;
    protected Chord detectedChord;

    protected double[] magnitudeSpectrum;

//	private double distanceThreshold = 0.7;

	private static double NOISE_CLASS_BITMASK_MAGNITUDE = 1;
//	private static double MAXIMUM_POSSIBLE_NOTES = 6;
    public ChordDetector(final int samplingFrequency, final int frameLength, final int frameShift, final ArrayList<Chord> targetChords) {
	    super();
        this.samplingFrequency = samplingFrequency;
        this.frameLength = frameLength;
        this.frameShift = frameShift;
        this.head = -1;
        this.atFrame = -1;
        this.maxFrames = -1;
        this.targetChords = targetChords;
    }

	protected void setTargetChords(ArrayList<Chord> chords){
		targetChords = chords;
	}

	private double[] getChromagram(double[] audioBuffer) {
		//Make sure the buffer length is even
		double[] tmpAudio;
		if ((audioBuffer.length % 2) == 0) {
			tmpAudio = audioBuffer.clone();
		} else {
			tmpAudio = new double[audioBuffer.length + 1];
			for (int i = 0; i < audioBuffer.length; i++) {
				tmpAudio[i] = audioBuffer[i];
			}
			tmpAudio[audioBuffer.length] = tmpAudio[audioBuffer.length - 1];
		}

		double[] buf = audioBuffer.clone();

//		readLock = true;
		magnitudeSpectrum = getMagnitudeSpectrum(buf);
		//Take the sqrt as part of the signal processing algorithm
		//TODO: verify if disabling this is better
//		for (int i = 0; i < magnitudeSpectrum.length; i++) {
//			magnitudeSpectrum[i] = Math.sqrt(magnitudeSpectrum[i]);
//		}
//		readLock = false;

		double A1 = 55; //reference note A1 in Hz
		double E1 = 82.4;
		double C3 = 130.81;
		int frequencyIndex;
		double[] chromagram = new double[12];
		Arrays.fill(chromagram, 0);
		int searchRange = 2;
//		int searchRangeBase = 2;
//		int searchRange = searchRangeBase;
		for (int interval = 0; interval < 12; interval++) {
//			interval = (i+7)%12;
//			interval = i;
			for (int phi = 1; phi <= 5; phi++) {
				for (int harmonic = 1; harmonic <= 2; harmonic++) {
//					searchRange = (int) Math.ceil(( (double)(searchRangeBase * phi * harmonic) / 3));
					frequencyIndex = (int) Math.round(MusicUtils.frequencyFromInterval(A1, interval) * (double) phi * (double) harmonic / ((double) samplingFrequency / (double) frameLength));
					chromagram[interval] += magnitudeSpectrum[frequencyIndex];
//					chromagram[interval] += magnitudeSpectrum[frequencyIndex] / harmonic;
//					chromagram[(interval+7)%12] += magnitudeSpectrum[frequencyIndex] / harmonic;
//					chromagram[interval] += findMaxValue(magnitudeSpectrum,frequencyIndex-searchRange,frequencyIndex+searchRange) / harmonic;
//					chromagram[(interval+3)%12] += findMaxValue(magnitudeSpectrum,frequencyIndex-searchRange,frequencyIndex+searchRange) / harmonic;
				}
			}
		}
		double maxVal = findMaxValue(chromagram, 0, chromagram.length - 1);
		for (int i = 0; i < chromagram.length; i++) {
			chromagram[i] /= maxVal;
		}
		return chromagram;
	}

	private Chord detectChord(ArrayList<Chord> targetChords, double[] chromagram) {
		//Take the square of chromagram so the peak differences are more pronounced. see paper.
		for (int i = 0; i < chromagram.length; i++) {
			chromagram[i] *= chromagram[i];
		}

		double[] deltas = new double[targetChords.size()];
		Arrays.fill(deltas, 0);
		for (int i = 0; i < targetChords.size(); i++) {
			deltas[i] = calculateDistanceToChord(chromagram, targetChords.get(i));
		}

//		int chordIndex = findMinIndex(deltas);
//		double minDistance = deltas[chordIndex];
//		if(minDistance > distanceThreshold){
//			return new Chord("X", "X");
//		}
		int chordIndex = findMaxIndex(deltas);

//		double DISTANCE_THRESHOLD = 0.8;
//		if(deltas[chordIndex] < DISTANCE_THRESHOLD){
//			return new Chord("X", "X");
//		}

		if (chordIndex > -1 && chordIndex < targetChords.size()) {
			return targetChords.get(chordIndex);
		} else {
			return new Chord("X", "X");
		}
	}



	public static double calculateDistanceToChord(double[] cgram, Chord chord) {
		double[] chromagram = cgram.clone();
		double[] bitMask = new double[12];
		int[] notes = chord.getNotes();
		Arrays.fill(bitMask, 0);
//		Arrays.fill(bitMask, 1);
		if(chord.toString().equals("noise")){
			for (int j = 0; j < bitMask.length; j++) {
				bitMask[j] = NOISE_CLASS_BITMASK_MAGNITUDE;
//				bitMask[j] = 0.5;
			}
		} else {

			for (int j = 0; j < notes.length; j++) {
				bitMask[notes[j] - 1] = 1;
//				bitMask[notes[j] - 1] = 0;
			}
		}

		double distance = 0;
		for (int j = 0; j < chromagram.length; j++) {
			distance += chromagram[j] * bitMask[j];
//			distance += Math.abs(bitMask[j] - chromagram[j]);
		}

		distance /= (double) notes.length;
		//Noise class doesn't really work effectively but a future review might solve that

		//Possible other metrics
//		distance /= (double) (12-notes.length); //yes, noise distance will return Inf, essentially disabling it for now
//		distance = Math.sqrt(1 - distance);
//		distance = Math.sqrt(distance);
//		distance = Math.pow(distance,1/12);
//		distance = 1 - distance;




		Log.d("chordDistance", "Chord: " + chord.toString() + " distance: " + Double.toString(distance));
		return distance;
	}

    @Override
    public void internalProcess(AudioData inputAudioData) {
            audioData = inputAudioData;
            //TODO: Fix this in PitchDetector too!
            if (audioData.length() > frameLength) {
                maxFrames = (int) Math.ceil((double) (audioData.length() - frameLength) / (double) frameShift);
            } else {
                maxFrames = 1;
            }
            atFrame = 1;
            head = 0;
            double[] chromagram;
            while ((tempBuffer = getNextFrame()) != null) {
                chromagram = getChromagram(tempBuffer);
                detectedChord = detectChord(targetChords, chromagram);
	            output = (double) targetChords.indexOf(detectedChord);
            }
    }

    @Override
    public void processingFinished() {

    }



}
