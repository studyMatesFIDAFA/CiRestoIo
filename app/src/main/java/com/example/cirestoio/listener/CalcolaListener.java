package com.example.cirestoio.listener;

import android.speech.tts.TextToSpeech;
import android.view.View;

import com.example.cirestoio.activity.Stat;

public class CalcolaListener implements View.OnClickListener{

    private Stat stat;

    public CalcolaListener(Stat stat)
    {
        this.stat = stat;
    }

    @Override
    public void onClick(View view) {
        // Calcola resto
        String importoStr = stat.getImporto().getText().toString();
        CharSequence importoErrato = "Inserire un importo corretto";
        double imp ;
        try {
            imp = Double.parseDouble(importoStr);
        }catch (Exception e) {
            stat.getResponse().setText(importoErrato);
            stat.getTs().speak(importoErrato, TextToSpeech.QUEUE_ADD, null, "importo errato");
            return ;
        }
        if (imp < 0){
            stat.getResponse().setText(importoErrato);
            stat.getTs().speak(importoErrato, TextToSpeech.QUEUE_ADD, null, "importo errato");
            return;
        }
        CharSequence result;
        if(stat.getSomma() > imp) {
            result ="Il resto ammonta a "+ String.format("%.02f", stat.getSomma()-imp) + " €";
        } else if (stat.getSomma() == imp) {
            result = "La somma fotografata coincide con l'importo da pagare";
        } else {
            result= "Per raggiungere l'importo indicato mancano ancora  "+ String.format("%.02f", imp-stat.getSomma())+ " €";
        }
        stat.getResponse().setText(result.toString());
        stat.getTs().speak(result,TextToSpeech.QUEUE_ADD, null,"resto");

    }
}
