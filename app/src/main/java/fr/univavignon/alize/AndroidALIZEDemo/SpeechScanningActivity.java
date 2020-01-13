package fr.univavignon.alize.AndroidALIZEDemo;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import org.kaldi.Assets;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;

public class SpeechScanningActivity extends BaseActivity implements RecognitionListener {
    static {
        System.loadLibrary("kaldi_jni");
    }

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;


    private AudioRecorder audioRecorder;
    private SpeakerVerifier speakerVerifier;
    private SpeechRecognizer speechRecognizer;
    private TextView statusTV, resultTV;
    private Model model;
    public final List<short[]> audioPackets = Collections.synchronizedList(new ArrayList<short[]>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_scanning);
        if (!checkPermission()) {
            requestPermission();
            return;
        }

        statusTV = findViewById(R.id.status_text_view);
        resultTV = findViewById(R.id.result_text_view);
        setUiState(STATE_START);


        // load model
        new SetupTask(this).execute();


        //speakerVerifier = new SpeakerVerifier(speakerVerificationManager, audioPackets);

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

               // startRecording();
            } else {
                Utils.makeToast(this, getResources().getString(R.string.permission_error_message));
                finish();
            }
        }
    }

    protected boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(SpeechScanningActivity.this, new
                String[]{RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
    }


    protected void startRecording() {



        audioRecorder.startRecord();
        //speechRecognizer.startRecognition();
        //speakerVerifier.startSpeakerVerifier();


    }


    protected void stopRecording() {

        audioRecorder.stopRecord();
        speechRecognizer.stopRecognition();
        //speakerVerifier.stopSpeakerVerifier();

        //afterRecordProcessing();
    }

    @Override
    public void onPartialResult(String s) {
        resultTV.append(s + "\n");
    }

    @Override
    public void onResult(String s) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }


    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<SpeechScanningActivity> activityReference;
        Model model;
        SetupTask(SpeechScanningActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                Log.d("!!!!", assetDir.toString());
                model = new Model(assetDir.toString() + "/model-android");
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                activityReference.get().setErrorState(result.getMessage());
            } else {
//                activityReference.get().speechRecognizer = new SpeechRecognizer(model,
//                        activityReference.get().audioPackets, activityReference.get());
                activityReference.get().audioRecorder = new AudioRecorder(model, activityReference.get().audioPackets);

                activityReference.get().startRecording();
                activityReference.get().setUiState(STATE_READY);
            }
        }
    }


    private void setUiState(int state) {
        switch (state) {
            case STATE_START:
                statusTV.setText(getResources().getString(R.string.initializing_status));

                break;
            case STATE_READY:
                statusTV.setText(getResources().getString(R.string.scanning_status));

                break;

        }
    }

    private void setErrorState(String message) {
        statusTV.setText(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (speechRecognizer != null) {
            speechRecognizer.stopRecognition();
        }
    }


}
