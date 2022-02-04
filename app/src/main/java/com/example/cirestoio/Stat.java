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
    static  double tagliToDouble[]= {5.0, 10.0, 20.0, 50.0, 100.0};


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
        //analyzeResults(results);
    }

    /*private void debugPrint(List<Detection> results) {
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
        int[] banconoteTrovate =new int[5];
        somma = 0 ; // altrimenti aggiungo alle analisi di foto precedenti
        for (Detection obj : results) {
            int label = Integer.parseInt(obj.getCategories().get(0).getLabel()); // label = {0,1,2,3,4}
            somma += tagliToDouble[label]; // incremento la somma totale dei contanti mostrati nell'immagine
            banconoteTrovate[label]++;  // incremento banconote trovate per quella label
        }
        //Aggiorno i campi
        numBanconote.setText(""+results.size());
        sommaText.setText(""+somma);
        tagli.setText(" Trovate "+banconoteTrovate[0]+" da "+tagliToString[0]+"\n");
        tagli.setText(" Trovate "+banconoteTrovate[1]+" da "+tagliToString[1]+"\n");
        tagli.setText(" Trovate "+banconoteTrovate[2]+" da "+tagliToString[2]+"\n");
        tagli.setText(" Trovate "+banconoteTrovate[3]+" da "+tagliToString[3]+"\n");
        tagli.setText(" Trovate "+banconoteTrovate[4]+" da "+tagliToString[4]+"\n");


    }*/

}
