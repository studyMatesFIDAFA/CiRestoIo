package com.example.cirestoio;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

import android.speech.tts.TextToSpeech;

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
    int numProcessamenti = 1;
    CharSequence num, s, lista;
    Detector d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

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

        ArrayAdapter adapter= new ArrayAdapter<String>(this,R.layout.selcted_item, new ArrayList<String>(MainActivity.countryRates.keySet()));
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

    public int getNumProcessamenti() {
        return numProcessamenti;
    }

    public void setNumProcessamenti( int numProcessamenti){
        this.numProcessamenti = numProcessamenti;
    }

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
}

