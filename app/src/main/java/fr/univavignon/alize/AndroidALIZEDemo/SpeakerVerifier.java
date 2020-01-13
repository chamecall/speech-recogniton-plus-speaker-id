package fr.univavignon.alize.AndroidALIZEDemo;

import android.media.AudioRecord;

import java.util.List;

import AlizeSpkRec.AlizeException;

public class SpeakerVerifier implements Runnable {
    protected Thread addSamplesThread = null;

    protected boolean recordExists = false;
    protected boolean recordError = false;
    public final List<short[]> audioPackets;
    private SpeakerVerificationManager speakerVerificationManager;

    public SpeakerVerifier(SpeakerVerificationManager speakerVerificationManager
            , List<short[]> audioPackets) {
        this.speakerVerificationManager = speakerVerificationManager;
        this.audioPackets = audioPackets;
    }

    public void startSpeakerVerifier() {
        if (recordExists) {
            try {
                speakerVerificationManager.resetAudio();
                speakerVerificationManager.resetFeatures();
            } catch (AlizeException e) {
                e.printStackTrace();
            }
            recordExists = false;
        }

        addSamplesThread = new Thread(this, "addSamples Thread");
        addSamplesThread.start();
    }

    public void stopSpeakerVerifier() {
        Utils.interruptThread(addSamplesThread);
        recordExists = !recordError;
    }

    public boolean getRecordExists() {
        return recordExists;
    }

    public boolean getRecordError() {
        return recordError;
    }

    @Override
    public void run() {
        recordError = false;

        short[] nextElement;

        while (!Thread.currentThread().isInterrupted() || (!audioPackets.isEmpty())) {
            nextElement = null;

            synchronized (audioPackets) {
                if (!audioPackets.isEmpty()) {
                    nextElement = audioPackets.get(0);
                    audioPackets.remove(0);
                }
            }
            if (nextElement != null) {
                System.out.println(nextElement.length);
                try {
                    speakerVerificationManager.addAudio(nextElement);
                } catch (Throwable e) {
                    e.printStackTrace();
                    recordError = true;
                }
            }
        }


    }
}
