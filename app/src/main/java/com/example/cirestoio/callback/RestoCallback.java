package com.example.cirestoio.callback;


import android.speech.RecognizerIntent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import com.example.cirestoio.activity.Stat;

import java.util.ArrayList;

public class RestoCallback implements ActivityResultCallback {

    public static final int RESULT_OK = -1;
    private final Stat stat;

    public RestoCallback(Stat stat)
    {
        this.stat = stat;
    }

    @Override
    public void onActivityResult(Object res) {
        ActivityResult result = (ActivityResult) res;
        if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
            ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String importo = frasi_riconosciute.get(0);
            stat.getImporto().setText(importo);
            stat.getCalcola().callOnClick();

        }

    }
}
