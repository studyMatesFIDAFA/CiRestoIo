package com.example.cirestoio.callback;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import com.example.cirestoio.activity.MainActivity;
import com.example.cirestoio.activity.Stat;
import com.example.cirestoio.model.Valuta;

import java.util.ArrayList;
import java.util.List;

public class ConvertiCallback implements ActivityResultCallback {

    public static final int RESULT_OK = -1;
    private final Stat stat;

    public ConvertiCallback(Stat stat)
    {
        this.stat = stat;
    }

    @Override
    public void onActivityResult(Object res) {
        ActivityResult result = (ActivityResult) res;
        if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
            //Ottengo le stringhe riconosciute dal speech to text
            ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String nazione = frasi_riconosciute.get(0);
            System.out.println(nazione);

            List<Valuta> valute = MainActivity.countryRates;
            boolean trovato = false;
            int posizione = -1;
            for(int i=0; i<valute.size() && !trovato; i++)
            {
                if(valute.get(i).getNazione().equalsIgnoreCase(nazione))
                {
                    trovato = true;
                    posizione = i;
                }
            }
            if(trovato)
            {
                this.stat.getElencoValute().setSelection(posizione);
            }
            else
            {
                MainActivity.textToSpeech.speak("Nazione non riconosciuta", TextToSpeech.QUEUE_ADD, null, "Nazione non trovata");
            }
        }
    }
}
