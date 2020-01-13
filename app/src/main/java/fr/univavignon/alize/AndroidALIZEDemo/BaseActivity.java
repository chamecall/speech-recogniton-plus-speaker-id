package fr.univavignon.alize.AndroidALIZEDemo;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.IOException;

import AlizeSpkRec.AlizeException;


public class BaseActivity extends AppCompatActivity {


    protected SpeakerVerificationManager speakerVerificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        try {
//            speakerVerificationManager = SpeakerVerificationManager.getSharedInstance(BaseActivity.this);
//        }
//        catch (AlizeException | IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                speakerVerificationManager.resetAudio();
                speakerVerificationManager.resetFeatures();
            } catch (AlizeException e) {
                e.printStackTrace();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
