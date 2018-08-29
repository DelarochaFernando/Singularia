package com.delarocha.singularia.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.delarocha.singularia.R;

public class MainActivity extends AppCompatActivity {

    public String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    //String READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE; 
    //String WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public Uri uri;
    public int CAMERA_REQUEST = 100;
    public int GALLERY_REQUES = 101;
    public int REQUEST_MULTIPLE_PERMISSIONS;


    private int mDialogType;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = ProgressDialog.show(MainActivity.this,"Cargando Datos","Cargando...");
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
       progress.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        },4000);


    }
}
