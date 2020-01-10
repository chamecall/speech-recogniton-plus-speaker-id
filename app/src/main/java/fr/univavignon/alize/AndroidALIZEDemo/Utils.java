package fr.univavignon.alize.AndroidALIZEDemo;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static void interruptThread(Thread thread) {
        try {
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            thread = null;
        }

    }

    public static void makeToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }}
