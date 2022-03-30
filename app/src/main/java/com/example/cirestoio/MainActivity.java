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
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity{
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    static final String API_URL = "https://tassidicambio.bancaditalia.it/terzevalute-wf-web/rest/v1.0/latestRates?lang={}";
    static Map<String,Double> countryRates = new HashMap<>();
    Button openCamera;
    ImageView im;
    public static TextToSpeech textToSpeech;
    private ActivityResultLauncher<Intent> startForResultOpenCamera, startForResultSpeechText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera = findViewById(R.id.openCamera);
        im = findViewById(R.id.imageView2);

        obtainPermission(this);

        /*
        //Se si usano listener dedicati esplode
        openCamera.setOnClickListener(new OpenCameraListener(this));
        im.setOnClickListener(new SpeechToTextClickListener(this));
        */

        this.startForResultSpeechText= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK  && result.getData() != null) {
                //Ottengo le stringhe riconosciute dal speech to text
                ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                System.out.println("ALGISE "+frasi_riconosciute.get(0));
            }});

        this.startForResultOpenCamera= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK  && result.getData() != null) {
                //Get image capture
                Bitmap captureImage = (Bitmap) result.getData().getExtras().get("data");
                Intent intent = new Intent(this, Stat.class);
                intent.putExtra("img", captureImage);
                startActivity(intent);
            }
        });

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

        /*
        Gestore dei click
         */
        View.OnClickListener gestore = new View.OnClickListener() {
            public void onClick(View view) {
                switch(view.getId()) {
                    case R.id.openCamera:
                        // Open camera
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startForResultOpenCamera.launch(takePictureIntent);
                        break;
                    case R.id.imageView2:
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startForResultSpeechText.launch(intent);
                        } else {
                            System.out.println("Non supporto del speech to text");
                        }
                        break;
                }
            }
        };

        openCamera.setOnClickListener(gestore);
        im.setOnClickListener(gestore);

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