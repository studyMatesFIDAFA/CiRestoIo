package com.example.cirestoio.listener;

import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;

import com.example.cirestoio.activity.MainActivity;
import com.example.cirestoio.activity.Stat;
import com.example.cirestoio.utils.Utils;

public class CurrencySelectionListener implements AdapterView.OnItemSelectedListener {

    private Stat stat;
    private int numProcessamenti;

    public CurrencySelectionListener(Stat stat)
    {
        this.stat = stat;
        this.numProcessamenti = 1;

    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String selectedCurrency = arg0.getItemAtPosition(arg2).toString();
        Double eurRate = MainActivity.countryRates.get(selectedCurrency);
        Double convertedValue = stat.getSomma() * eurRate;
        CharSequence result = String.format("%.02f",convertedValue) + " " + selectedCurrency;
        stat.getConversion().setText(result.toString());
        if(numProcessamenti > 1)
            stat.getTs().speak(result, TextToSpeech.QUEUE_ADD, null,"conversione");
        else {
            stat.getTs().speak("L'importo totale è "+stat.getS(),TextToSpeech.QUEUE_ADD, null,"totale");
            stat.getTs().speak(Utils.getCorrectString(Integer.parseInt(stat.getNum().toString())),TextToSpeech.QUEUE_ADD, null,"numero");
            stat.getTs().speak(Utils.getCorrectString(stat.getLista()),TextToSpeech.QUEUE_ADD, null,"lista");
        }
        numProcessamenti++;
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        stat.getConversion().setText("");
    }
}
