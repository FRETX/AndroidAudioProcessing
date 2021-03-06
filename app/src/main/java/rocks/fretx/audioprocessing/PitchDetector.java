package rocks.fretx.audioprocessing;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Onur Babacan on 9/23/16.
 */
//Uses the YIN algorithm

public class PitchDetector extends AudioAnalyzer {

    private final double threshold;

    private double[] tempBuffer;
    private final float[] yinBuffer;

    protected PitchDetectionResult result;
    private final int nLastValues = 9;
    protected float[] lastValues = new float[nLastValues];
    protected float medianPitch = -1;

    //TODO: parameter-less constructor with default values
    private static final double DEFAULT_THRESHOLD = 0.20;
    private static final int DEFAULT_SAMPLING_FREQUENCY = 44100;
    //We arbitrarily set the lower bound of detection to 50Hz. At least two pitch periods are needed
    //So the default frame length is: (1/50) * 44100 * 2 = 1764
    public static final int DEFAULT_FRAME_LENGTH = 1764;
    //An overlap of 50% is good enough for real-time applications
    public static final int DEFAULT_FRAME_SHIFT = 882;

    @Override
    public void internalProcess(AudioData inputAudioData) {
            audioData = inputAudioData;
            if (audioData.length() < frameLength) {
                maxFrames = (int) Math.ceil((double) (audioData.length() - frameLength) / (double) frameShift);
            } else {
                maxFrames = 1;
            }
            atFrame = 1;
            head = 0;
            while ((tempBuffer = getNextFrame()) != null) {
                result = getPitch(tempBuffer);
	            output = result.getPitch();
//                Log.d("YIN", Float.toString(result.getPitch()));
            }
    }

    @Override
    public void processingFinished() {

    }

    public PitchDetector(final int samplingFrequency, final int frameLength, final int frameShift, final double threshold) {
	    super();
        this.samplingFrequency = samplingFrequency;
        this.frameLength = frameLength;
        this.frameShift = frameShift;
        this.threshold = threshold;
        this.head = -1;
        this.atFrame = -1;
        this.maxFrames = -1;
        this.yinBuffer = new float[frameLength / 2];
        this.tempBuffer = new double[frameLength];
        this.result = new PitchDetectionResult();
        Arrays.fill(lastValues,-1);
    }

    public PitchDetectionResult getPitch(final double[] audioBufferDouble) {

        int tauEstimate;
        float pitchInHertz;
        float[] audioBuffer = new float[audioBufferDouble.length];
	    for (int i = 0; i < audioBufferDouble.length; i++) {
		    audioBuffer[i] = (float) audioBufferDouble[i];
	    }

        difference(audioBuffer);
        cumulativeMeanNormalizedDifference();
        tauEstimate = absoluteThreshold();
        if (tauEstimate != -1) {
            final float betterTau = parabolicInterpolation(tauEstimate);
            pitchInHertz = (float) samplingFrequency / betterTau;
            if(pitchInHertz > samplingFrequency/4){ //This is mentioned in the YIN paper
                pitchInHertz = -1;
            }
        } else{
            // no pitch found
            pitchInHertz = -1;
        }
        result.setPitch(pitchInHertz);

        for (int i = lastValues.length -1 ; i > 0; i--) {
            lastValues[i] = lastValues[i-1];
        }
        lastValues[0] = pitchInHertz;
        updateMedianPitch();

        return result;
    }

    private void updateMedianPitch(){
        int pitchedValuesCount = 0;
        for (int i = 0; i < lastValues.length ; i++) {
            if(lastValues[i] > 0){
                pitchedValuesCount++;
            }
        }
        if(pitchedValuesCount > 3){
            float[] sortedPitchValues = new float[pitchedValuesCount];
            int y = 0;
            for (int i = 0; i < lastValues.length ; i++) {
                if(lastValues[i] > 0){
                    sortedPitchValues[y] = lastValues[i];
                    y++;
                }
            }
            Arrays.sort(sortedPitchValues);
            medianPitch = PitchDetector.median(sortedPitchValues);
        } else medianPitch = -1;
    }

    private void difference(final float[] audioBuffer) {
        Arrays.fill(yinBuffer,0);
        for  (int tau = 1; tau < yinBuffer.length; tau++) {
            for (int t = 0; t < yinBuffer.length; t++) {
                yinBuffer[tau] += (float) Math.pow(audioBuffer[t] - audioBuffer[t + tau],2);
            }
        }
    }

    private void cumulativeMeanNormalizedDifference() {
        int tau;
        yinBuffer[0] = 1;
        float runningSum = 0;
        for (tau = 1; tau < yinBuffer.length; tau++) {
            runningSum += yinBuffer[tau];
            yinBuffer[tau] = yinBuffer[tau] / runningSum * tau;
        }
    }

    private int absoluteThreshold() {
        int tau = 0;
        int i = 0;
        boolean tauFound = false;

        while(!tauFound && (i+1 < yinBuffer.length - 1)){
            if(yinBuffer[i] < threshold){
                if(i+1 == yinBuffer.length - 1) break;
                while(yinBuffer[i+1] < yinBuffer[i]){
                    tau = i++;
                }
                tauFound = true;
                result.setProbability(1 - yinBuffer[tau]);
            } else i++;
        }

        if(!tauFound){
            tau = -1;
            result.setProbability(0);
            result.setPitched(false);
        } else result.setPitched(true);

        return tau;
    }

    private float parabolicInterpolation(final int tauEstimate) {
        final float betterTau;
        if(tauEstimate > 0 && tauEstimate < yinBuffer.length-1){
            float y1,y2,y3;
            y1 = yinBuffer[tauEstimate-1];
            y2 = yinBuffer[tauEstimate];
            y3 = yinBuffer[tauEstimate+1];
            betterTau = tauEstimate + (y3 - y1) / (2 * (2 * y2 - y3 - y1));
        } else {
            //TODO: Implement proper boundary conditions
            betterTau = tauEstimate;
        }
        return betterTau;
    }

}
