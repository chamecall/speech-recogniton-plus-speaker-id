package fr.univavignon.alize.AndroidALIZEDemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

import AlizeSpkRec.AlizeException;

public class MainActivity extends AppCompatActivity {


    protected SpeakerVerificationManager speakerVerificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            speakerVerificationManager = SpeakerVerificationManager.getSharedInstance(this);
//        } catch (IOException | AlizeException e) {
//            e.printStackTrace();
//        }
        switchVoiceInfoStatus(true);

    }


    @Override
    public void onResume() {
        try {
            super.onResume();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void updateAvailableFunctionality() {
        if (!speakerVerificationManager.speakerExists()) switchVoiceInfoStatus(false);
        else switchVoiceInfoStatus(true);
    }

    public void init_speaker(View view) {
        startActivity(new Intent(this, SpeakersListActivity.class));
    }


    private void switchVoiceInfoStatus(boolean isRecorded) {

        if (isRecorded) {
            findViewById(R.id.launch_btn).setEnabled(true);
        } else {
            findViewById(R.id.launch_btn).setEnabled(false);
        }
    }

    public void run_scanning(View view) {
        startActivity(new Intent(this, SpeechScanningActivity.class));
    }
}
