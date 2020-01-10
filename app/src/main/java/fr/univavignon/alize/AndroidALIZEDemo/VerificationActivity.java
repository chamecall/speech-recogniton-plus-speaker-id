package fr.univavignon.alize.AndroidALIZEDemo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.SimpleSpkDetSystem;


public class VerificationActivity extends RecordActivity {

    private final int ERROR_COLOR = Color.RED;
    private final int SUCCESS_COLOR = Color.rgb(0, 150, 0);

    private TextView resultText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            setContentView(R.layout.verification);

            timeText = findViewById(R.id.timeText);

            super.onCreate(savedInstanceState);

            resultText = findViewById(R.id.result_text);
            startRecordButton = findViewById(R.id.startBtn);
            stopRecordButton = findViewById(R.id.stopBtn);


            startRecordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultText.setText("");
                    startRecording();
                }
            });

            stopRecordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopRecording();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void afterRecordProcessing() {
        String result = "";
        resultText.setTextColor(ERROR_COLOR);

        if (super.getRecordError()) {
            resultText.setText(result);
            Utils.makeToast(this, getResources().getString(R.string.recording_not_completed));
            return;
        }


        try {
            SimpleSpkDetSystem.SpkRecResult verificationResult = speakerVerificationManager.verifySpeaker(speakerVerificationManager.getSpeakerId());

            if (verificationResult.match) {
                result = getString(R.string.verify_match_text) + "\n" + getString(R.string.match_score) + "\n" + verificationResult.score;
                resultText.setTextColor(SUCCESS_COLOR);
            } else {
                result = getString(R.string.match_score) + "\n" + verificationResult.score;
                resultText.setTextColor(ERROR_COLOR);
            }
        }  catch (Throwable e) { //TODO catch proper exception
            e.printStackTrace();
            result = getString(R.string.verification_failed_not_enough_features);
        }

        resultText.setText(result);

        try {
            speakerVerificationManager.resetAudio();
            speakerVerificationManager.resetFeatures();
        } catch (AlizeException e) {
            e.printStackTrace();
        }
    }
}
