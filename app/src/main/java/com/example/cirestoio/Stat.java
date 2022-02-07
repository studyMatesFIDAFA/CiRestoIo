package com.example.cirestoio;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.List;

public class Stat extends AppCompatActivity implements View.OnClickListener  {

    static  String tagliToString[]= {"5 €", "10 €", "20 €", "50 €", "100 €"};
    static int NUM_INDEX=5; // NUMERO DI TAGLI

    ImageView iv;
    Button calcola;
    EditText numBanconote;
    EditText tagli;
    EditText importo;
    EditText sommaText;
    EditText response;
    double somma=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        //BINDING
        iv = findViewById(R.id.imageView);
        calcola = findViewById(R.id.CalcolaResto);
        numBanconote = findViewById(R.id.numBanconote);
        tagli = findViewById(R.id.listaTagli);
        importo = findViewById(R.id.importo);
        response = findViewById(R.id.response);
        sommaText = findViewById(R.id.sommaText);
        calcola.setOnClickListener(this);

        // Set image on ImageView
        Bitmap captureImage = (Bitmap) getIntent().getExtras().get("img");
        iv.setImageBitmap(captureImage);
        try {
            runObjectDetection(captureImage);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        // Calcola resto
        String importoStr = importo.getText().toString();
        double imp = 0;
        try {
            imp = Double.parseDouble(importoStr);
        }catch (Exception e) {
            response.setText("Inserire un importo corretto");
            return ;
        }
        if (imp < 0){
            response.setText("Inserire un importo corretto");
            return;
        }

        String tot;
        if(somma > imp) {
            tot=String.format("%.02f", somma-imp);
            response.setText("Il resto ammonta a "+ tot + " €");
        } else if (somma == imp){
            response.setText("La somma fotografata coincide con l'importo da pagare");
        } else {
            tot=String.format("%.02f", imp-somma);
            response.setText("Per raggiungere l'importo indicato mancano ancora  "+ tot+ " €");
        }

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
        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(this, "banconote_pascal3.tflite",options) ;
        // Do l'immagine in pasto al detector e recupero i risultati
        List<Detection> results = detector.detect(image);

        for (Detection obj : results) {
            Category category = obj.getCategories().get(0);
            String text = ""+category.getLabel()+", "+ category.getScore()+"\n";

        }

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
        numBanconote.setText(""+results.size());
        sommaText.setText(""+String.format("%.02f",somma)+" €");
        tagli.setText("");
        for (int i=0; i<NUM_INDEX; i++){
            if(banconoteTrovate[i]>0) {
                if (banconoteTrovate[i] == 1)
                    tagli.append("Trovata " + banconoteTrovate[i] + " banconota da " + tagliToString[i] + "\n");
                else
                    tagli.append("Trovate " + banconoteTrovate[i] + " banconote da " + tagliToString[i] + "\n");
            }
        }


    }

}
