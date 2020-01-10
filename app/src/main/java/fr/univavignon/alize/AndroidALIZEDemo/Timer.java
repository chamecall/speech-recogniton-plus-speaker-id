package fr.univavignon.alize.AndroidALIZEDemo;

import android.media.AudioRecord;
import android.os.Handler;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Timer {
    protected long startTime;
    protected Thread timerThread = null;
    protected Handler handler;
    protected TextView timeTextView;

    public Timer(TextView timeTextView) {
        handler = new Handler();
        this.timeTextView = timeTextView;
    }

    public void startTimer() {
        timeTextView.setText(R.string.default_time);
        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.currentTimeMillis();
                while (!Thread.currentThread().isInterrupted()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            long currentTime = System.currentTimeMillis() - startTime;
                            String result = new SimpleDateFormat("mm:ss:SS", Locale.getDefault())
                                    .format(new Date(currentTime));
                            timeTextView.setText(result);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

            }
        }, "Timer Thread");
        timerThread.start();
    }

    public void stopTimer() {
        Utils.interruptThread(timerThread);

    }


}
