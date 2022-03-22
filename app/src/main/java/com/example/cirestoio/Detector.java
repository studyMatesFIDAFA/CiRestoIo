package com.example.cirestoio;

import android.graphics.Bitmap;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.List;

public class Detector {
    static final String[] tagliToString = {"5 €", "10 €", "20 €", "50 €", "100 €"};
    Stat stat;

    public Detector(Stat stat) {
        this.stat = stat;
    }

    public void detect(Bitmap bitmap) throws IOException {
        //Converte l'immagine da Bitmap a TensorImage
        TensorImage image = TensorImage.fromBitmap(bitmap);
        // Imposto l'object detector creando prima le opzioni e poi passandole all'object detector
        ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(10).setScoreThreshold(0.6f).build();
        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(stat, "banconote3.tflite", options);
        // Do l'immagine in pasto al detector e recupero i risultati
        long time0 = System.currentTimeMillis();
        List<Detection> results = detector.detect(image);
        long time1 = System.currentTimeMillis();
        System.out.println("TEMPO INFERENZA: " + (time1 - time0));
        analyzeResults(results);
    }

    private void analyzeResults(List<Detection> results) {
        int[] banconoteTrovate = new int[stat.getNumIndex()];
        double somma = 0; // altrimenti aggiungo alle analisi di foto precedenti
        for (Detection obj : results) {
            String label = obj.getCategories().get(0).getLabel();
            Double doubleVal = Utils.getBanknoteDoubleVal(label);
            somma += doubleVal; // incremento la somma totale dei contanti mostrati nell'immagine
            int index = Utils.getBanknoteIndex(label);
            if (index != -1)
                banconoteTrovate[index]++;  // incremento banconote trovate per quella label
            else
                System.out.println("----------------Errore index--------------------");
        }
        //Aggiorno i campi
        stat.setNum("" + results.size());
        ;
        stat.getNumBanconote().setText(stat.getNum().toString());
        stat.setS(String.format("%.02f", somma) + " €");
        stat.getSommaText().setText(stat.getS().toString());


        CharSequence lista = "";
        for (int i = 0; i < stat.getNumIndex(); i++) {
            if (banconoteTrovate[i] > 0) {
                if (banconoteTrovate[i] == 1)
                    lista += "Trovata " + banconoteTrovate[i] + " banconota da " + tagliToString[i] + "\n";
                else
                    lista += "Trovate " + banconoteTrovate[i] + " banconote da " + tagliToString[i] + "\n";
            }
        }
        stat.getTagli().setText(lista.toString());

    }
}
