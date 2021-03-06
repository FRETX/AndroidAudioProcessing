package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 9/23/16.
 */


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AudioInputHandler implements Runnable {

    public boolean isPaused = false;
    public boolean isFinished = false;
    private boolean bufferAvailable = false;
    private Object pauseLock = new Object();

    private final String TAG = "AudioInputHandler";
    private AudioRecord audioInputStream;
    private short [] audioBufferTemp;
    protected short [] audioBuffer;

    protected final int samplingFrequency;
    protected final int audioBufferSize;

    private List<AudioAnalyzer> audioAnalyzers;
    private List<ParameterAnalyzer> parameterAnalyzers;

	private double volume;

    private static final int DEFAULT_SAMPLING_FREQUENCY = 44100;
    private static final int DEFAULT_AUDIO_BUFFER_SIZE = 7200;

    public AudioInputHandler(){
        this(DEFAULT_SAMPLING_FREQUENCY,DEFAULT_AUDIO_BUFFER_SIZE);
    }

    public AudioInputHandler(int fs){
        this(fs,DEFAULT_AUDIO_BUFFER_SIZE);
    }

    public AudioInputHandler(int fs, int bufSize){
        samplingFrequency = fs;
        audioBufferSize = bufSize;
        audioBuffer = new short[audioBufferSize];
        audioBufferTemp = new short[audioBufferSize];
        audioAnalyzers = new CopyOnWriteArrayList<AudioAnalyzer>();


        int maxSamplingFrequency = getMaxSamplingFrequency();

        if(samplingFrequency <= maxSamplingFrequency){
            int minBufferSize = getMinBufferSize(samplingFrequency);
            int minBufferSizeInSamples =  minBufferSize/2;
            if(minBufferSizeInSamples <= audioBufferSize ){
                audioInputStream = new AudioRecord(
                        MediaRecorder.AudioSource.MIC, samplingFrequency,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        audioBufferSize * 2);
	            Log.d("AudioInputHandler","audio stream initialized");
            }else{
                throw new IllegalArgumentException("Buffer size must be at least " + (minBufferSize *2));
            }
        } else {
            throw new IllegalArgumentException("Sampling frequency must be at least " + maxSamplingFrequency);
        }
    }

    public void run(){
	    //Set thread priority to Audio
        int tid=android.os.Process.myTid();
        android.os.Process.setThreadPriority(tid, Process.THREAD_PRIORITY_AUDIO);

        audioInputStream.startRecording();
        while(!isFinished){
            int samplesRead = audioInputStream.read(audioBufferTemp,0,audioBufferSize);
            if(samplesRead != audioBufferSize){
                Log.v(TAG, "Could not read audio data");
                bufferAvailable = false;
            } else {
                bufferAvailable = true;
                audioBuffer = audioBufferTemp.clone();
                AudioData audioData = new AudioData(audioBuffer,samplingFrequency);
//                Log.d("pre-norm power", Double.toString(audioData.getSignalPower()));
                audioData.normalize();
//                Log.d("post-norm power", Double.toString(audioData.getSignalPower()));
	            volume = 10 * Math.log10(audioData.getSignalPower());
//	            Log.d("post-norm volume", Double.toString(volume));
                for(AudioAnalyzer analyzer : audioAnalyzers){
                    analyzer.process(audioData);
                }
            }
        }
    }

	public double getVolume(){
		return volume;
	}

    public boolean isBufferAvailable(){ return bufferAvailable; };

    public void onDestroy(){
        isFinished = true;
        bufferAvailable = false;
        releaseInputStream();
    }

    public void releaseInputStream(){
        audioInputStream.stop();
        audioInputStream.release();
        bufferAvailable = false;
        Log.d(TAG,"Audio recording stopped and stream released");
    }

    public void addAudioAnalyzer (final AudioAnalyzer audioAnalyzer){
        audioAnalyzers.add(audioAnalyzer);
    }

    public void removeAudioAnalyzer (final AudioAnalyzer audioAnalyzer){
        audioAnalyzers.remove(audioAnalyzer);
    }



    public static int getMaxSamplingFrequency() {
        int maxSamplingFrequency = 0;
        for (int fs : new int[] {8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(fs, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                maxSamplingFrequency = fs;
            }
        }
        return maxSamplingFrequency;
    }

    public static int getMinBufferSize(int fs){
        return AudioRecord.getMinBufferSize(fs, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

}
