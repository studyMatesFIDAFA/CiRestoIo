package com.example.cirestoio.callback;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cirestoio.activity.MainActivity;
import com.example.cirestoio.activity.Stat;

import java.util.ArrayList;
import java.util.Locale;


public class ComandoCallback implements ActivityResultCallback  {

    private Stat stat;

    public ComandoCallback(Stat stat)
    {
        this.stat = stat;
    }

    public static final int RESULT_OK = -1;

    @Override
    public void onActivityResult(Object res) {
        ActivityResult result = (ActivityResult) res;
        if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
            //Ottengo le stringhe riconosciute dal speech to text
            ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String comando = frasi_riconosciute.get(0);
            System.out.println(comando);
            if (comando.equalsIgnoreCase("calcola"))
            {
                this.stat.mic(0);
            }
            else if (comando.equalsIgnoreCase("converti"))
            {
                this.stat.mic(1);
            }
            else {
                MainActivity.textToSpeech.speak("Comando non riconosciuto", TextToSpeech.QUEUE_ADD, null, "comando non trovato");
            }
        }
    }



}
