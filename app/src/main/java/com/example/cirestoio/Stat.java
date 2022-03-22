package com.example.cirestoio;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.speech.tts.TextToSpeech;

public class Stat extends AppCompatActivity implements View.OnClickListener {
    static final String[] tagliToString = {"5 €", "10 €", "20 €", "50 €", "100 €"};
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
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        // Text to Speech
        ts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ts.setLanguage(Locale.ITALY);
                }
            }
        });

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
        calcola.setOnClickListener(this);
        ripeti.setOnClickListener(new RepeatListener(this));

        elencoValute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selectedCurrency = arg0.getItemAtPosition(arg2).toString();
                Double eurRate = MainActivity.countryRates.get(selectedCurrency);
                Double convertedValue = somma * eurRate;
                CharSequence result = String.format("%.02f",convertedValue) + " " + selectedCurrency;
                conversion.setText(result.toString());
                if(numProcessamenti > 1)
                    ts.speak(result,TextToSpeech.QUEUE_ADD, null,"conversione");
                else {
                    ts.speak("L'importo totale è "+s,TextToSpeech.QUEUE_ADD, null,"totale");
                    ts.speak("Sono state rilevate "+num+" banconote",TextToSpeech.QUEUE_ADD, null,"numero");
                    ts.speak(lista,TextToSpeech.QUEUE_ADD, null,"lista");
                }
                numProcessamenti++;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                conversion.setText("");
            }
        });


        // Set image on ImageView
        Bitmap captureImage = (Bitmap) getIntent().getExtras().get("img");
        iv.setImageBitmap(captureImage);
        try {
            runObjectDetection(captureImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter= new ArrayAdapter<String>(this,R.layout.selcted_item, new ArrayList<String>(MainActivity.countryRates.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        elencoValute.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        // Calcola resto
        String importoStr = importo.getText().toString();
        String importoErrato = "Inserire un importo corretto";
        double imp ;
        try {
            imp = Double.parseDouble(importoStr);
        }catch (Exception e) {
            response.setText(importoErrato);
            return ;
        }
        if (imp < 0){
            response.setText(importoErrato);
            return;
        }

        CharSequence result;
        if(somma > imp) {
            result ="Il resto ammonta a "+ String.format("%.02f", somma-imp) + " €";
        } else if (somma == imp) {
           result = "La somma fotografata coincide con l'importo da pagare";
        } else {
           result= "Per raggiungere l'importo indicato mancano ancora  "+ String.format("%.02f", imp-somma)+ " €";
        }
        response.setText(result.toString());
        ts.speak(result,TextToSpeech.QUEUE_ADD, null,"resto");

    }

    private Double getBanknoteDoubleVal(String label) {
        switch (label) {
            case "5euro":
                return 5.00;
            case "10euro":
                return 10.00;
            case "20euro":
                return 20.00;
            case "50euro":
                return 50.00;
            case "100euro":
                return 100.00;
            default:
                return 0.00;
        }
    }

    private int getBanknoteIndex(String label) {
        switch (label) {
            case "5euro":
                return 0;
            case "10euro":
                return 1;
            case "20euro":
                return 2;
            case "50euro":
                return 3;
            case "100euro":
                return 4;
            default:
                return -1;
        }
    }

    protected void runObjectDetection(Bitmap bitmap) throws  IOException {
        //Converte l'immagine da Bitmap a TensorImage
        TensorImage image = TensorImage.fromBitmap(bitmap) ;
        // Imposto l'object detector creando prima le opzioni e poi passandole all'object detector
        ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(10).setScoreThreshold(0.6f).build() ;
        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(this, "banconote3.tflite",options) ;
        // Do l'immagine in pasto al detector e recupero i risultati
        long time0 = System.currentTimeMillis();
        List<Detection> results = detector.detect(image);
        long time1 = System.currentTimeMillis();
        System.out.println("TEMPO INFERENZA: "+(time1-time0));

        analyzeResults(results);
    }

    private void analyzeResults(List<Detection> results) {
        int[] banconoteTrovate =new int[5];
        somma = 0 ; // altrimenti aggiungo alle analisi di foto precedenti
        for (Detection obj : results) {
            String label = obj.getCategories().get(0).getLabel();
            Double doubleVal = getBanknoteDoubleVal(label);
            somma += doubleVal; // incremento la somma totale dei contanti mostrati nell'immagine
            int index = getBanknoteIndex(label);
            if(index!=-1)
                banconoteTrovate[index]++;  // incremento banconote trovate per quella label
            else
                System.out.println("----------------Errore index--------------------");
        }
        //Aggiorno i campi
        num = ""+results.size();
        numBanconote.setText(num.toString());
        s = String.format("%.02f",somma)+" €";
        sommaText.setText(s.toString());


        lista = "";
        for (int i=0; i<NUM_INDEX; i++){
            if(banconoteTrovate[i]>0) {
                if (banconoteTrovate[i] == 1)
                    lista += "Trovata " + banconoteTrovate[i] + " banconota da " + tagliToString[i] + "\n";
                else
                    lista += "Trovate " + banconoteTrovate[i] + " banconote da " + tagliToString[i] + "\n";
            }
        }
        tagli.setText(lista.toString());

    }

    public static String[] getTagliToString() {
        return tagliToString;
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

    public CharSequence getNum() {
        return num;
    }

    public CharSequence getS() {
        return s;
    }

    public CharSequence getLista() {
        return lista;
    }
}

