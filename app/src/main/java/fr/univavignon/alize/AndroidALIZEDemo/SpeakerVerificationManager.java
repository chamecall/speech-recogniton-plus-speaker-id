package fr.univavignon.alize.AndroidALIZEDemo;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.IdAlreadyExistsException;
import AlizeSpkRec.SimpleSpkDetSystem;


public class SpeakerVerificationManager extends SimpleSpkDetSystem {

    private static SpeakerVerificationManager speakerVerificationManager;
    private final String modelDataFileName = "modelData";
    private static Context context;

    private SpeakerVerificationManager(InputStream configInput, String workdirPath) throws AlizeException, IOException {
        super(configInput, workdirPath);
    }

    static SpeakerVerificationManager getSharedInstance(Context appContext) throws IOException, AlizeException {
        if (speakerVerificationManager == null) {
            context = appContext;
            InputStream configAsset = appContext.getAssets().open("AlizeDefault.cfg");
            speakerVerificationManager = new SpeakerVerificationManager(configAsset, appContext.getFilesDir().getPath());
            configAsset.close();

            InputStream backgroundModelAsset = appContext.getAssets().open("gmm/world.gmm");
            speakerVerificationManager.loadBackgroundModel(backgroundModelAsset);
            backgroundModelAsset.close();

            speakerVerificationManager.loadSavedModels();
        }
        return speakerVerificationManager;
    }

    boolean speakerExists() {
        boolean speakerExists = false;
        try {
            speakerExists = super.speakerCount() > 0;
        } catch (AlizeException e) {
            e.printStackTrace();
        }
        return speakerExists;
    }

    String getSpeakerId() {
        if (!speakerExists()) return "";
        return Speaker.getId();
    }


    private void loadSavedModels() throws AlizeException, IOException {
        if (!getModelState()) return;
        removeSpeaker();
        super.loadSpeakerModel(Speaker.getId(), Speaker.getId());
        setModelState(true);

    }

    void addAndCreateSpeakerModel() throws IdAlreadyExistsException, AlizeException {
        removeSpeaker();
        System.out.println("speakers len is " + super.speakerCount());
        super.createSpeakerModel(Speaker.getId());
        super.saveSpeakerModel(Speaker.getId(), Speaker.getId());
        setModelState(true);

    }


    void removeSpeaker() throws AlizeException {
        for (String id : super.speakerIDs()) {
            super.removeSpeaker(id);
        }
        setModelState(false);
        // super.adaptSpeakerModel(Speaker.getId());
    }

    void setModelState(boolean state) {
        try {
            FileOutputStream fos = context.openFileOutput(modelDataFileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(state);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean getModelState() {
        boolean modelState = false;
        try {
            FileInputStream fis = context.openFileInput(modelDataFileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            modelState = (Boolean) is.readObject();
            is.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return modelState;
    }

}
