package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 9/24/16.
 */

public class AudioData {

//    protected short[] audioBuffer;
	protected double[] audioBuffer;
    protected final int samplingFrequency;

    public AudioData(short[] aBuf , int fs){
//        audioBuffer = aBuf.clone();
	    audioBuffer = AudioAnalyzer.shortToDouble(aBuf);
        samplingFrequency = fs;
    }

    public int length(){
        return audioBuffer.length;
    }

	public void normalize(){
		double[] tmpBuffer = audioBuffer.clone();
		for (int i = 0; i < tmpBuffer.length; i++) {
			tmpBuffer[i] = Math.abs(tmpBuffer[i]);
		}
		double maxVal = AudioAnalyzer.findMaxValue(tmpBuffer,0, tmpBuffer.length-1);
		for (int i = 0; i < audioBuffer.length; i++) {
			audioBuffer[i] = audioBuffer[i]/maxVal*0.99;
		}
	}

    public double getSignalPower(){
        double acc = 0;
	    double normalized;
        for (int i = 0; i < audioBuffer.length; i++) {
//	        normalized = ((((double) audioBuffer[i] / 32768)) )*10;//values are really low without the *10! yup. dirty hack.
	        //TODO: make device-specific volume normalization
//	        acc +=  normalized * normalized;
	        acc += audioBuffer[i] * audioBuffer[i];
        }
	    return acc / (double) audioBuffer.length;
    }
}
