package fr.univavignon.alize.AndroidALIZEDemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import AlizeSpkRec.AlizeException;


public class EditSpeakerModelActivity extends SpeakerVerificationFitActivity {

    private Button updateSpeakerButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.edit_speaker_model);
        timeText = findViewById(R.id.timeText);
        super.onCreate(savedInstanceState);

        startRecordButton = findViewById(R.id.startBtn);
        stopRecordButton = findViewById(R.id.stopBtn);
        updateSpeakerButton = findViewById(R.id.update_speaker_button);

        updateSpeakerButton.setEnabled(false);



        updateSpeakerButton.setOnClickListener(updateSpeakerListener);

        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
    }



    private View.OnClickListener updateSpeakerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                speakerVerificationManager.addAndCreateSpeakerModel();

                speakerVerificationManager.resetAudio();
                speakerVerificationManager.resetFeatures();

                finish();
            } catch (AlizeException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
                Utils.makeToast(getApplicationContext(), getResources().getString(R.string.recording_not_completed));
            }
        }
    };

    protected void afterRecordProcessing() {
        if (super.getEmptyRecord() || !super.getRecordExists()) {
            updateSpeakerButton.setEnabled(false);
            Utils.makeToast(this, getResources().getString(R.string.no_sound_detected_recoloc));
        } else updateSpeakerButton.setEnabled(true);

    }
}