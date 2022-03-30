package com.example.cirestoio;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    static final String API_URL = "https://tassidicambio.bancaditalia.it/terzevalute-wf-web/rest/v1.0/latestRates?lang={}";
    static Map<String,Double> countryRates = new HashMap<>();
    Button openCamera;
    ImageView im;
    public static TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera = findViewById(R.id.openCamera);
        im = findViewById(R.id.imageView2);

        obtainPermission(this);

        openCamera.setOnClickListener(new OpenCameraListener(this));
        im.setOnClickListener(new SpeechToTextClickListener(this));

        // Text to Speech
        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ITALY);
                }
            }
        });

        new ApiRequest().execute(API_URL);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean obtainPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //i permessi per l'uso della camera non sono stati concessi, quindi bisogna richiederli
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
        // a questo punto i permessi per la camera sono già stati concessi
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //i permessi per l'uso del microfono non sono stati concessi, quindi bisogna richiederli
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        // a questo punto anche i permessi per il microfono sono già stati concessi, per cui è possibile procedere ad operare
        return true;
    }

}