package rocks.fretx.audioprocessing;

import android.support.annotation.Nullable;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
	protected double output;
    protected boolean enabled = true;
	private List<ParameterAnalyzer> parameterAnalyzers;

	public AudioAnalyzer(){
		parameterAnalyzers = new CopyOnWriteArrayList<ParameterAnalyzer>();
	}

    protected abstract void internalProcess(AudioData audioData);
    protected abstract void processingFinished();


	public void addParameterAnalyzer(final ParameterAnalyzer parameterAnalyzer) {
		parameterAnalyzers.add(parameterAnalyzer);
	}

	public void removeParameterAnalyzer(final ParameterAnalyzer parameterAnalyzer) {
		parameterAnalyzers.remove(parameterAnalyzer);
	}

	public void process(AudioData audioData){
		if(enabled){
			internalProcess(audioData);
			processingFinished();
			sendOutput(output);
		}
	}

	private void sendOutput(double output){
		for (ParameterAnalyzer analyzer : parameterAnalyzers) {
			analyzer.process(output);
		}
	}

    @Nullable
    protected double[] getNextFrame() {
        double[] outputBuffer;
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

	public void enable(){
		enabled = true;
	}

	public void disable(){
		enabled = false;
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

	public static double[] getMagnitudeSpectrum(double[] buf) {
		//FFT
		double[] window = getHammingWindow(buf.length);
		for (int i = 0; i < buf.length; i++) {
			buf[i] *= window[i];
		}
		DoubleFFT_1D fft = new DoubleFFT_1D(buf.length);
		fft.realForward(buf);

		//Calculate Magnitude spectrum from FFT result
		double[] magnitudeSpectrum = new double[buf.length / 2];
		magnitudeSpectrum[0] = Math.sqrt(Math.abs(buf[0]));
		magnitudeSpectrum[magnitudeSpectrum.length - 1] = Math.sqrt(Math.abs(buf[1]));
		for (int i = 1; i < magnitudeSpectrum.length - 1; i++) {
			magnitudeSpectrum[i] = Math.sqrt((buf[2 * i] * buf[2 * i]) + (buf[2 * i + 1] * buf[2 * i + 1]));
		}
		//Normalize by the largest peak
		double maxVal = 0;
		for (int i = 0; i < magnitudeSpectrum.length; i++) {
			if (magnitudeSpectrum[i] > maxVal) maxVal = magnitudeSpectrum[i];
		}
		double normalizationFactor = maxVal;
		for (int i = 0; i < magnitudeSpectrum.length; i++) {
			magnitudeSpectrum[i] /= normalizationFactor;
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
        double maxVal = -1* Double.MAX_VALUE;
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

	public static int findMaxIndex(double[] arr) {
		double maxVal = -1*Double.MAX_VALUE;
		int maxIndex = -1;

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > maxVal) {
				maxVal = arr[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
