package com.example.cirestoio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class OpenCameraListener extends AppCompatActivity implements View.OnClickListener{

    private MainActivity mainActivity;
    private ActivityResultLauncher<Intent> startForResult;

    public OpenCameraListener (MainActivity mainActivity){

        this.mainActivity = mainActivity;
        this.startForResult= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK  && result.getData() != null) {
                //Get image capture
                Bitmap captureImage = (Bitmap) result.getData().getExtras().get("data");
                Intent intent = new Intent(mainActivity, Stat.class);
                intent.putExtra("img", captureImage);
                startActivity(intent);
            }
        });
    }

    public void onClick(View view) {
        // Open camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startForResult.launch(takePictureIntent);
    }
}
