package rocks.fretx.audioprocessing;

import android.util.Log;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Onur Babacan on 10/20/16.
 */

public class ChordDetector extends AudioAnalyzer {


    private short[] tempBuffer;
    private List<Chord> targetChords;
    protected Chord detectedChord;

    //Plot
    protected double[] magnitudeSpectrum;
	protected double volume = 0;
    protected boolean readLock = true;

    public ChordDetector(final int samplingFrequency, final int frameLength, final int frameShift, final List<Chord> targetChords) {
	    super();
//        int frameLength = Math.round ((float)audioData.samplingFrequency * CHROMAGRAM_FRAME_LENGTH_IN_S);
//        int frameShift = frameLength / 4;
//        final float CHROMAGRAM_FRAME_LENGTH_IN_S = 0.75f;
        this.samplingFrequency = samplingFrequency;
        this.frameLength = frameLength;
        this.frameShift = frameShift;
        this.head = -1;
        this.atFrame = -1;
        this.maxFrames = -1;
        this.targetChords = targetChords;
    }

    private double[] getChromagram(short[] audioBuffer){
        //Make sure the buffer length is even
        short[] tmpAudio;
        if((audioBuffer.length % 2) == 0){
            tmpAudio = audioBuffer.clone();
        } else{
            tmpAudio = new short[audioBuffer.length+1];
            for (int i = 0; i < audioBuffer.length; i++) {
                tmpAudio[i] = audioBuffer[i];
            }
            tmpAudio[audioBuffer.length] = 0;
        }

        double[] buf = shortToDouble(audioBuffer);
	    for (int i = 0; i < buf.length; i++) {
		    buf[i] -= 0.5; //center the signal on 0 before windowing
	    }

        readLock = true;
	    magnitudeSpectrum = getMagnitudeSpectrum(buf);
        readLock = false;



        double A1 = 55; //reference note A1 in Hz
        int peakSearchWidth = 2;
	    int kprime,k0,k1;
	    double[] chromagram = new double[12];
	    Arrays.fill(chromagram,0);
        for (int interval = 0; interval < 12; interval++) {
            for (int phi = 1; phi <= 5; phi++) {
                for (int harmonic = 1; harmonic <= 2; harmonic++) {
                    kprime = (int) Math.round( MusicUtils.frequencyFromInterval(A1,interval) * (double)phi * (double)harmonic / ((double)samplingFrequency/(double)frameLength) );
                    k0 = kprime - (peakSearchWidth*harmonic);
                    k1 = kprime + (peakSearchWidth*harmonic);
                    chromagram[interval] += findMaxValue(magnitudeSpectrum, k0, k1) / harmonic;
                }
            }
        }

	    return chromagram;
    }

    private Chord detectChord(List<Chord> targetChords, double[] chromagram){
        //Take the square of chromagram so the peak differences are more pronounced. see paper.
        for (int i = 0; i < chromagram.length; i++) {
            chromagram[i] *= chromagram[i];
        }

        double[] deltas = new double[targetChords.size()];
        Arrays.fill(deltas,0);
        double[] bitMask = new double[12];
        for (int i = 0; i < targetChords.size(); i++) {
            //Generate bit mask for target chord
            Arrays.fill(bitMask,1);
            int[] notes = targetChords.get(i).getNotes();
            for (int j = 0; j < notes.length; j++) {
                bitMask[notes[j]-1] = 0;
            }
	        //Calculate the normalized total difference with target chord pattern
            for (int j = 0; j < chromagram.length; j++) {
                deltas[i] += chromagram[j] * bitMask[j];
            }
            deltas[i] /= 12-notes.length;
            deltas[i] = Math.sqrt(deltas[i]);
        }
        int chordIndex = findMinIndex(deltas);
        return targetChords.get(chordIndex);
    }

    @Override
    public void internalProcess(AudioData inputAudioData) {
            audioData = inputAudioData;
            volume = audioData.getSignalPower();
//		Log.d("volume",Double.toString(volume));
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
