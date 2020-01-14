package fr.univavignon.alize.AndroidALIZEDemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.List;



public class AudioRecorder  implements Runnable{

    protected static final int RECORDER_SAMPLE_RATE = 16000;
    protected static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    protected static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    protected AudioRecord recorder;
    public final List<short[]> audioPackets;
    // Collections.synchronizedList(new ArrayList<short[]>());
    private int minInternalBufferSize;
    protected boolean emptyRecord = false;

    protected Thread recordingThread = null;

    public AudioRecorder(List<short[]> audioPackets) {
        this.audioPackets = audioPackets;

        minInternalBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, minInternalBufferSize * 4);



    }

    public void startRecord() {

        recorder.startRecording();
        recordingThread = new Thread(this, "AudioRecorder Thread");
        recordingThread.start();

    }

    public void stopRecord() {
        if (recordingThread == null) {
            return;
        }

        if (recorder != null) {
            recorder.stop();
        }
        Utils.interruptThread(recordingThread);
    }

    public boolean getEmptyRecord() {
        return emptyRecord;
    }



    @Override
    public void run() {
        emptyRecord = true;
        short[] tmpAudioSamples = new short[minInternalBufferSize];
        while (!Thread.currentThread().isInterrupted()) {
            int samplesRead = recorder.read(tmpAudioSamples, 0, minInternalBufferSize);

            if (samplesRead == minInternalBufferSize) {
                short[] samples = new short[samplesRead];
                //System.arraycopy(tmpAudioSamples, 0, samples, 0, samplesRead);
                for (int i=0; i < samples.length; i++) {
                    samples[i] = tmpAudioSamples[i];
                    if (samples[i] != 0) {
                        emptyRecord = false;
                    }
                }

                synchronized (audioPackets) {
                    audioPackets.add(samples);
                }
            }
        }
    }

    public void destroy() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
