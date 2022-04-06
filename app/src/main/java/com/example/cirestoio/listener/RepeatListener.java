package com.example.cirestoio.listener;

import android.speech.tts.TextToSpeech;
import android.view.View;

import com.example.cirestoio.activity.Stat;
import com.example.cirestoio.utils.Utils;

public class RepeatListener implements View.OnClickListener{

    private Stat stat;

    public RepeatListener(Stat stat)
    {
        this.stat=stat;
    }
    @Override
    public void onClick(View view) {
        TextToSpeech ts = stat.getTs();
        ts.speak("L'importo totale è "+stat.getS(),TextToSpeech.QUEUE_ADD, null,"totale");
        ts.speak(Utils.getCorrectString(Integer.parseInt(stat.getNum().toString())), TextToSpeech.QUEUE_ADD, null, "numero");
        ts.speak(Utils.getCorrectString(stat.getLista()),TextToSpeech.QUEUE_ADD, null,"lista");
    }
}
