package com.example.cirestoio.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.cirestoio.model.Valuta;
import com.example.cirestoio.utils.Permission;
import com.example.cirestoio.utils.ApiRequest;
import com.example.cirestoio.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity  {
    static final String API_URL = "https://tassidicambio.bancaditalia.it/terzevalute-wf-web/rest/v1.0/latestRates?lang={}";
    public static List<Valuta> countryRates = new ArrayList<Valuta>();
    Button openCamera;
    ImageView im;
    ConstraintLayout layout;
    public static TextToSpeech textToSpeech;
    private ActivityResultLauncher<Intent> startForResultOpenCamera, startForResultSpeechText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera = findViewById(R.id.openCamera);
        im = findViewById(R.id.imageView2);
        layout = findViewById(R.id.layout);

        Permission.obtainPermission(this);


        this.startForResultSpeechText = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                //Ottengo le stringhe riconosciute dal speech to text
                ArrayList<String> frasi_riconosciute = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String comando = frasi_riconosciute.get(0);
                if (comando.equalsIgnoreCase("fotocamera"))
                    openCamera.callOnClick();
                else {
                    textToSpeech.speak("Comando non riconosciuto", TextToSpeech.QUEUE_ADD, null, "comando non trovato");
                }
            }
        });

        this.startForResultOpenCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                //Get image capture
                Bitmap captureImage = (Bitmap) result.getData().getExtras().get("data");
                Intent intent = new Intent(this, Stat.class);
                intent.putExtra("img", captureImage);
                startActivity(intent);
            }
        });

        // Text to Speech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ITALY);
                }
                textToSpeech.speak("Clicca lo schermo e pronuncia fotocamera o premi il pulsante per aprire la camera", TextToSpeech.QUEUE_ADD, null, "comando iniziale");
            }
        });

        new ApiRequest().execute(API_URL);
       /* try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // Listener bottone
        openCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Open camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startForResultOpenCamera.launch(takePictureIntent);
            }
        });


        // Listener Layout
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startForResultSpeechText.launch(intent);
                } else {
                    System.out.println("Non supporto del speech to text");
                }
                return true;

            }
        });
    }


}
