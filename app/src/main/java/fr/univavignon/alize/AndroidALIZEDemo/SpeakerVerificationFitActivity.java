package fr.univavignon.alize.AndroidALIZEDemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static android.Manifest.permission.RECORD_AUDIO;


public class SpeakerVerificationFitActivity extends BaseActivity {


    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;


    protected Button startRecordButton, stopRecordButton;
    private AudioRecorder audioRecorder;
    private SpeakerVerifier speakerVerifier;
    private Timer timer;
    protected TextView timeText;

    public final List<short[]> audioPackets = Collections.synchronizedList(new ArrayList<short[]>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        audioRecorder = new AudioRecorder(audioPackets);
        speakerVerifier = new SpeakerVerifier(speakerVerificationManager, audioPackets);
        timer = new Timer(timeText);
    }

    protected boolean getRecordExists() {
        return speakerVerifier.getRecordExists();
    }

    protected boolean getEmptyRecord() {
        return audioRecorder.getEmptyRecord();
    }

    protected boolean getRecordError() {
        return speakerVerifier.getRecordError();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Utils.makeToast(this, getResources().getString(R.string.permission_error_message));
            }
        }
    }

    protected boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(SpeakerVerificationFitActivity.this, new
                String[]{RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
    }


    protected void startRecording() {
        if (!checkPermission()) {
            requestPermission();
            return;
        }

        startRecordButton.setVisibility(View.INVISIBLE);
        stopRecordButton.setVisibility(View.VISIBLE);

        audioRecorder.startRecord();
        speakerVerifier.startSpeakerVerifier();
        timer.startTimer();


    }


    protected void stopRecording() {
        stopRecordButton.setVisibility(View.INVISIBLE);
        startRecordButton.setVisibility(View.VISIBLE);
        audioRecorder.stopRecord();
        speakerVerifier.stopSpeakerVerifier();
        timer.stopTimer();

        afterRecordProcessing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRecorder.destroy();
    }

    /**
     * Method meant to be inherited and implemented with the post recording processes.
     */
    protected void afterRecordProcessing() {
    }
}
