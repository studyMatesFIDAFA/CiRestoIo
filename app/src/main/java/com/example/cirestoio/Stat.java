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
import java.util.HashMap;
import java.util.List;

public class Stat extends AppCompatActivity implements View.OnClickListener  {

    static  String tagliToString[]= {"5 €", "10 €", "20 €", "50 €", "100 €"};
    //static  double tagliToDouble[]= {5.0, 10.0, 20.0, 50.0, 100.0};

    /*classifica.put(1, "Juventus");
    classifica.put(2, "Napoli");
    classifica.put(3, "Roma");
    classifica.put(4, "Inter");
    for(Integer key : classifica.keySet()) {
        System.out.println(classifica.get(key));
    }*/


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
            System.out.println("ERRORE!!!!!");
            response.setText("Inserire un importo corretto");
            return ;
        }

        double tot;
        if(somma > imp) {
            tot=somma-imp;
            response.setText("Il resto ammonta a "+ tot + " €");
        } else if (somma == imp){
            response.setText("La somma fotografata coincide con l'importo da pagare");
        } else {
            tot=imp-somma;
            response.setText("Per raggiungere l'importo indicato mancano ancora  "+ tot+ " €");
        }

    }

    private Double getBanknoteDoubleVal(String label) {
        switch (label) {
            case "5euro":
                return 5.0;
            case "10euro":
                return 10.0;
            case "20euro":
                return 20.0;
            case "50euro":
                return 50.0;
            case "100euro":
                return 100.0;
            default:
                return 0.0;
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
        ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(5).setScoreThreshold(0.5f).build() ;
        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(this, "banconote_pascal.tflite",options) ;
        // Do l'immagine in pasto al detector e recupero i risultati
        List<Detection> results = detector.detect(image);

        /*val resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, text)
        }*/

        for (Detection obj : results) {
            Category category = obj.getCategories().get(0);
            String text = ""+category.getLabel()+", "+ category.getScore()+"\n";
            System.out.println("--------ALGISE-----------------");
            System.out.println(text);
        }
        //debugPrint(results); // solo per debug
        analyzeResults(results);
    }

    private void debugPrint(List<Detection> results) {
        for (Detection obj : results){
            for (int j=0;j<obj.getCategories().size();j++) {
                Category cat = obj.getCategories().get(j);
                System.out.println("Label "+j+": "+cat.getLabel());
                System.out.println("Banconota da "+tagliToString[j]);
                int confidence = Math.round(cat.getScore());
                System.out.println("Confidence "+confidence);
            }
        }
    }

    private void analyzeResults(List<Detection> results) {
        System.out.println("ANALYZE RESULTS");
        int[] banconoteTrovate =new int[5];
        somma = 0 ; // altrimenti aggiungo alle analisi di foto precedenti
        for (Detection obj : results) {
            String label = obj.getCategories().get(0).getLabel(); // label = {5euro,10euro,20euro,50euro,100euro}
            Double doubleVal = getBanknoteDoubleVal(label);
            System.out.println("Val Double "+doubleVal);
            somma += doubleVal; // incremento la somma totale dei contanti mostrati nell'immagine
            int index = getBanknoteIndex(label);
            if(index!=-1)
                banconoteTrovate[index]++;  // incremento banconote trovate per quella label
            else
                System.out.println("----------------Errore index--------------------");
        }
        //Aggiorno i campi
        numBanconote.setText(""+results.size());
        sommaText.setText(""+somma+" €");
        tagli.setText("");
        if(banconoteTrovate[0]>0)
            tagli.append(" Trovate "+banconoteTrovate[0]+" banconote da "+tagliToString[0]+"\n");
        if(banconoteTrovate[1]>0)
            tagli.append(" Trovate "+banconoteTrovate[1]+" banconote da "+tagliToString[1]+"\n");
        if (banconoteTrovate[2]>0)
            tagli.append(" Trovate "+banconoteTrovate[2]+" banconote da "+tagliToString[2]+"\n");
        if (banconoteTrovate[3]>0)
            tagli.append(" Trovate "+banconoteTrovate[3]+" banconote da "+tagliToString[3]+"\n");
        if (banconoteTrovate[4]>0)
            tagli.append(" Trovate "+banconoteTrovate[4]+" banconote da "+tagliToString[4]+"\n");


    }

}
