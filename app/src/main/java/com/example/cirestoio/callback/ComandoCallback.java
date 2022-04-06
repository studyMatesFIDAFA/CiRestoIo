package com.example.cirestoio.callback;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResult;

import com.example.cirestoio.activity.MainActivity;

import java.util.ArrayList;



public class ComandoCallback implements ActivityResultCallback {
    public static final int RESULT_OK = -1;

    @Override
    public void onActivityResult(Object res) {
        ActivityResult result = (ActivityResult) res;
        if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
            //Ottengo le stringhe riconosciute dal speech to text
            ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String comando = frasi_riconosciute.get(0);
            System.out.println(comando);
            if (comando.contains("resto"))
            {

            }
            else if (comando.contains("converti"))
            {

            }
            else {
                MainActivity.textToSpeech.speak("Comando non riconosciuto", TextToSpeech.QUEUE_ADD, null, "comando non trovato");
            }
        }
    }
}
