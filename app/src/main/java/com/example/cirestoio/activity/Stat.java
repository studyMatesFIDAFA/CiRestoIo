package com.example.cirestoio.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.speech.tts.TextToSpeech;

import com.example.cirestoio.callback.ComandoCallback;
import com.example.cirestoio.callback.ConvertiCallback;
import com.example.cirestoio.callback.RestoCallback;
import com.example.cirestoio.detection.Detector;
import com.example.cirestoio.R;
import com.example.cirestoio.listener.CalcolaListener;
import com.example.cirestoio.listener.CurrencySelectionListener;
import com.example.cirestoio.listener.RepeatListener;
import com.example.cirestoio.model.Valuta;

public class Stat extends AppCompatActivity {
    static final int NUM_INDEX=5; // NUMERO DI TAGLI

    ImageView iv;
    Button calcola;
    Button ripeti;
    EditText numBanconote;
    EditText tagli;
    EditText importo;
    EditText sommaText;
    EditText response;
    Spinner elencoValute;
    EditText conversion;
    TextToSpeech ts;
    double somma=0;
    CharSequence num, s, lista;
    Detector d;
    private ActivityResultLauncher<Intent> startForResultSpeechText;
    private ActivityResultLauncher<Intent> startForResultResto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new RestoCallback(this));;
    private ActivityResultLauncher<Intent> startForResultConverti = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ConvertiCallback(this));;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        this.startForResultSpeechText = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ComandoCallback(this));

        ts = MainActivity.textToSpeech;

        //BINDING
        iv = findViewById(R.id.imageView);
        calcola = findViewById(R.id.CalcolaResto);
        ripeti = findViewById(R.id.ripeti);
        numBanconote = findViewById(R.id.numBanconote);
        tagli = findViewById(R.id.listaTagli);
        importo = findViewById(R.id.importo);
        response = findViewById(R.id.response);
        sommaText = findViewById(R.id.sommaText);
        elencoValute = findViewById(R.id.valute);
        conversion = findViewById(R.id.conversion);
        calcola.setOnClickListener(new CalcolaListener(this));
        ripeti.setOnClickListener(new RepeatListener(this));
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startForResultSpeechText.launch(intent);
                } else {
                    System.out.println("Non supporto del speech to text");
                }
            }
        });

        elencoValute.setOnItemSelectedListener(new CurrencySelectionListener(this));

        // Set image on ImageView
        Bitmap captureImage = (Bitmap) getIntent().getExtras().get("img");
        iv.setImageBitmap(captureImage);
        try {
            d = new Detector(this);
            d.detect(captureImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> listaValute = new ArrayList<>();
        for(Valuta v : MainActivity.countryRates)
        {
            listaValute.add(v.getNome());
        }
        ArrayAdapter adapter= new ArrayAdapter<String>(this,R.layout.selcted_item, listaValute);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        elencoValute.setAdapter(adapter);

    }

    public static int getNumIndex() {
        return NUM_INDEX;
    }

    public ImageView getIv() {
        return iv;
    }

    public Button getCalcola() {
        return calcola;
    }

    public EditText getNumBanconote() {
        return numBanconote;
    }

    public EditText getTagli() {
        return tagli;
    }

    public EditText getImporto() {
        return importo;
    }

    public EditText getSommaText() {
        return sommaText;
    }

    public EditText getResponse() {
        return response;
    }

    public Spinner getElencoValute() {
        return elencoValute;
    }

    public EditText getConversion() {
        return conversion;
    }

    public TextToSpeech getTs() {
        return ts;
    }

    public double getSomma() {
        return somma;
    }

    public void setSomma ( double somma ) { this.somma = somma; }

    public CharSequence getNum() {
        return num;
    }

    public void setNum (CharSequence num ) {
        this.num = num;
    }

    public CharSequence getS() {
        return s;
    }

    public void setS (CharSequence s ) {
        this.s = s;
    }

    public CharSequence getLista() {
        return lista;
    }

    public void setLista( CharSequence lista ){ this.lista = lista ;}

    public void mic (int i){
        if (i == 0){
            MainActivity.textToSpeech.speak("Pronuncia l'importo da calcolare", TextToSpeech.QUEUE_ADD, null, "resto");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            if (intent.resolveActivity(getPackageManager()) != null) {
                startForResultResto.launch(intent);
            } else {
                System.out.println("Non supporto del speech to text");
            }

        }
        else if (i == 1){
            MainActivity.textToSpeech.speak("Pronuncia la valuta o la nazione da convertire", TextToSpeech.QUEUE_ADD, null, "resto");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            if (intent.resolveActivity(getPackageManager()) != null) {
                startForResultConverti.launch(intent);
            } else {
                System.out.println("Non supporto del speech to text");
            }
        }
    }
}

