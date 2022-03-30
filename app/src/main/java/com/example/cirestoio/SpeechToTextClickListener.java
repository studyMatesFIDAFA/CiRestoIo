package com.example.cirestoio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextClickListener extends AppCompatActivity implements View.OnClickListener{

    private MainActivity mainAct;
    private ActivityResultLauncher<Intent> startForResult;
    private String command;
    public SpeechToTextClickListener(MainActivity mainAct){
        this.mainAct = mainAct;
        this.command = "";
        this.startForResult= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result != null && result.getResultCode() == RESULT_OK  && result.getData() != null) {
                        //Ottengo le stringhe riconosciute dal speech to text
                        ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        this.command = frasi_riconosciute.get(0);
                        System.out.println("ALGISE "+this.command);
                    }});
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startForResult.launch(intent);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }
}
