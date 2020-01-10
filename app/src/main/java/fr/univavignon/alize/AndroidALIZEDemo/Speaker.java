package fr.univavignon.alize.AndroidALIZEDemo;

import java.io.Serializable;

public class Speaker implements Serializable {
    private static final long serialVersionUID = -5435670920302756945L;
    private static final String ID = "42";
    private static Speaker speaker;

    private Speaker() {

    }

//    public static Speaker getSpeaker() {
//        if (speaker == null) {
//            speaker = new Speaker();
//        }
//        return speaker;
//    }


    public static String getId() {
        return ID;
    }


}