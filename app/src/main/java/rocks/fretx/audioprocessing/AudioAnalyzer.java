package rocks.fretx.audioprocessing;

import android.support.annotation.Nullable;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;

/**
 * Created by Onur Babacan on 9/23/16.
 */

abstract public class AudioAnalyzer {

    protected int samplingFrequency;
    protected int frameShift;
    protected int frameLength;
    protected int head;
    protected int atFrame;
    protected int maxFrames;
    protected AudioData audioData;

    public abstract void process(AudioData audioData);
    public abstract void processingFinished();

    @Nullable
    protected short[] getNextFrame() {
        short[] outputBuffer;
        if (atFrame <= maxFrames) {
            atFrame++;
            if (head + frameLength > audioData.length()) {
                //zero pad the end
                outputBuffer = (Arrays.copyOf(Arrays.copyOfRange(audioData.audioBuffer, head, audioData.length() - 1), frameLength)).clone();
                head = audioData.length() - 1;
                return outputBuffer;
            } else {
                //get regular frame
                outputBuffer = Arrays.copyOfRange(audioData.audioBuffer, head, head + frameLength);
                head = head + frameShift - 1;
                return outputBuffer;
            }

        } else {
            //return null to signal that the end is reached
            return null;
        }
    }

    //General purpose and utility methods
    public static float[] shortToFloat(short[] audio) {
        float[] output = new float[audio.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (float)audio[i] / 32768f;
        }
        return output;
    }

    public static double[] shortToDouble(short[] audio) {
        double[] output = new double[audio.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (double)audio[i] / 32768;
        }
        return output;
    }

    public static double[] getHammingWindow(int windowLength) {
        double alpha = 0.54;
        double beta = 1 - alpha;
        double[] window = new double[windowLength];

        for (int i = 0; i < windowLength; i++) {
            window[i] = alpha - beta * Math.cos((2 * Math.PI * i) / (windowLength - 1));
        }

        return window;
    }

    public static double[] getMagnitudeSpectrum(double[] buf){
        //FFT
        double[] window = getHammingWindow(buf.length);
        for (int i = 0; i < buf.length; i++) {
            buf[i] *= window[i];
        }
        DoubleFFT_1D fft = new DoubleFFT_1D(buf.length);
        fft.realForward(buf);

        //Calculate Magnitude spectrum from FFT result
        double[] magnitudeSpectrum = new double[buf.length / 2];
        magnitudeSpectrum[0] = Math.abs(buf[0]);
        magnitudeSpectrum[magnitudeSpectrum.length - 1] = Math.abs(buf[1]);
        for (int i = 1; i < magnitudeSpectrum.length - 1; i++) {
            magnitudeSpectrum[i] = ((buf[2 * i] * buf[2 * i]) + (buf[2 * i + 1] * buf[2 * i + 1]));
        }
        //Take the sqrt of values necessary for calculating the spectrum, at the same time,
        //normalize by the largest peak
        double maxVal = 0;
        for (int i = 0; i < magnitudeSpectrum.length; i++) {
            if (magnitudeSpectrum[i] > maxVal) maxVal = magnitudeSpectrum[i];
        }
        double normalizationFactor = maxVal;
        for (int i = 0; i < magnitudeSpectrum.length; i++) {
            magnitudeSpectrum[i] = Math.sqrt(magnitudeSpectrum[i]) / normalizationFactor;
        }
        return magnitudeSpectrum;
    }

    public static float median(float[] m) {
        int middle = m.length / 2;
        if (m.length % 2 == 1) {
            return m[middle];
        } else {
            return (m[middle - 1] + m[middle]) / 2;
        }
    }

    //Utility methods
    public static double findMaxValue(double[] arr, int beginIndex, int endIndex) {
        //TODO: array safety
        double maxVal = -Double.MAX_VALUE;
        for (int i = beginIndex; i <= endIndex; i++) {
            if (arr[i] > maxVal) maxVal = arr[i];
        }
        return maxVal;
    }

    public static int findMinIndex(double[] arr) {
        double minVal = Double.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < minVal) {
                minVal = arr[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}
