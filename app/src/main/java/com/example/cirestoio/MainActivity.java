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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    static final String API_URL = "https://tassidicambio.bancaditalia.it/terzevalute-wf-web/rest/v1.0/latestRates?lang={}";
    static Map<String,Double> countryRates = new HashMap<>();
    Button openCamera;
    public static TextToSpeech textToSpeech;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RESULT_OK  && result.getData() != null) {
            //Get image capture
            Bitmap captureImage = (Bitmap) result.getData().getExtras().get("data");
            Intent intent = new Intent(MainActivity.this, Stat.class);
            intent.putExtra("img", captureImage);
            startActivity(intent);
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera = findViewById(R.id.openCamera);

        obtainPermission(this);

        openCamera.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        // Open camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startForResult.launch(takePictureIntent);
    }

}