package fr.univavignon.alize.AndroidALIZEDemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import AlizeSpkRec.AlizeException;


public class SpeakersListActivity extends BaseActivity {

    private Button identifyButton, removeSpeakerButton;
    private TextView voiceExistingInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speakers_list);


        voiceExistingInfo = findViewById(R.id.voiceInfo);
        removeSpeakerButton = findViewById(R.id.removeall_button);
        identifyButton = findViewById(R.id.identify_button);

        updateAvailableFunctionality();

    }

    private void switchVoiceInfoStatus(boolean isRecorded) {

        if (isRecorded) {
            voiceExistingInfo.setText(R.string.voice_recorded);
            voiceExistingInfo.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            identifyButton.setEnabled(true);
            removeSpeakerButton.setEnabled(true);
        } else {
            voiceExistingInfo.setText(R.string.voice_not_recorded);
            voiceExistingInfo.setBackgroundColor(getResources().getColor(R.color.black));
            identifyButton.setEnabled(false);
            removeSpeakerButton.setEnabled(false);
        }
    }


    public void updateSpeaker(View v) {
        startActivity(new Intent(this, EditSpeakerModelActivity.class));
    }

    public void identify(View v) {
        startActivity(new Intent(this, VerificationActivity.class));
    }

    public void removeSpeaker(View v) {
        try {
            speakerVerificationManager.removeSpeaker();
            switchVoiceInfoStatus(false);
        } catch (AlizeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            updateAvailableFunctionality();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private void updateAvailableFunctionality() {
        if (!speakerVerificationManager.speakerExists()) switchVoiceInfoStatus(false);
        else switchVoiceInfoStatus(true);
    }




}
