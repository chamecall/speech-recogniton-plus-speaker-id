package fr.univavignon.alize.AndroidALIZEDemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.kaldi.KaldiRecognizer;
import org.kaldi.Model;

import java.util.List;


public class AudioRecorder implements Runnable {

    protected static final int RECORDER_SAMPLE_RATE = 16000;
    protected static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    protected static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    protected AudioRecord recorder;
    public final List<short[]> audioPackets;
    // Collections.synchronizedList(new ArrayList<short[]>());
    private int minInternalBufferSize;
    protected boolean emptyRecord = false;

    protected Thread recordingThread = null;

    private KaldiRecognizer recognizer;

    public AudioRecorder(Model model, List<short[]> audioPackets) {
        this.audioPackets = audioPackets;

        minInternalBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, minInternalBufferSize * 4);
        recognizer = new KaldiRecognizer(model, 16000.0f);


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
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = tmpAudioSamples[i];
                    if (samples[i] != 0) {
                        emptyRecord = false;
                    }
                }


                //System.out.println(samples.length);

                boolean isFinal = recognizer.AcceptWaveform(samples, samples.length);


                String result;
                if (isFinal) {
                    result = recognizer.Result();
                    System.out.println("FINAL RESULT IS " + result);
//                        System.out.println("FINAL IS " + result);
//                        if (!result.isEmpty()) {
//                            mainHandler.post(new ResultEvent(recognizer.Result(), true));
//                        }
                } else {
                    result = recognizer.PartialResult();
                    result = result.split(":")[1];
                    result = result.substring(2, result.length() - 2);
                    if (!result.isEmpty()) {
                        final String finalResult = result;
                        System.out.println("PARTIAL RES: " + finalResult);
//                            mainHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    listener.onPartialResult(finalResult);
//                                }
//                            });

                    }

                }


//                synchronized (audioPackets) {
//                    audioPackets.add(samples);
//                }
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
