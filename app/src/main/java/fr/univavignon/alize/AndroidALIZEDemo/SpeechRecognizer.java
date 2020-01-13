package fr.univavignon.alize.AndroidALIZEDemo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.kaldi.KaldiRecognizer;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import AlizeSpkRec.AlizeException;

public class SpeechRecognizer implements Runnable{

    private final List<short[]> audioPackets;
    private KaldiRecognizer recognizer;
    private RecognitionListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Thread recognitionThread = null;



    public SpeechRecognizer(Model model, List<short[]> audioPackets, RecognitionListener listener) {
        recognizer = new KaldiRecognizer(model, 16000.0f);
        this.listener = listener;

        this.audioPackets = audioPackets;
    }



    public void startRecognition() {

        recognitionThread = new Thread(this, "KaldiThread");
        recognitionThread.start();
    }

    public void stopRecognition() {
        Utils.interruptThread(recognitionThread);
    }



    @Override
    public void run() {
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

                    boolean isFinal = recognizer.AcceptWaveform(nextElement, nextElement.length);


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
                        result = result.substring(2, result.length()-2);
                        if (!result.isEmpty()) {
                            final String finalResult = result;
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //listener.onPartialResult(finalResult);
                                }
                            });

                        }

                    }


            }
        }

    }




}
