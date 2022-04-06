package com.example.cirestoio.callback;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import com.example.cirestoio.activity.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class RestoCallback implements ActivityResultCallback {

    public static final int RESULT_OK = -1;
    @Override
    public void onActivityResult(Object res) {
        ActivityResult result = (ActivityResult) res;
        if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
            ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String importo = frasi_riconosciute.get(0);
            System.out.println(importo);


        }

    }
}
