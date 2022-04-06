package com.example.cirestoio.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission {
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    public static boolean obtainPermission(Activity activity) {
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
