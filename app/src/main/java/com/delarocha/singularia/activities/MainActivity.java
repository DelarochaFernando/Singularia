package com.delarocha.singularia.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
//import android.support.v7.app.AppCompatActivity;

import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //public String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    //String READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE; 
    //String WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public Uri uri;
    public int CAMERA_REQUEST = 100;
    public int GALLERY_REQUES = 101;
    public int REQUEST_MULTIPLE_PERMISSIONS;


    private int mDialogType;
    private Tools tools;
    private Context context = this;
    private Handler handler;
    private Account mAccount;
    private DocumentReference docRef;
    private FirebaseFirestore mFireStoreDB;
    private String email, pass;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tools = new Tools(context);
        //progress = ProgressDialog.show(MainActivity.this,"Cargando Datos","Cargando...");
        mFireStoreDB = FirebaseFirestore.getInstance();
       //progress.show();
        handler = new Handler();
        mAccount = new Account();
        verifyUserLoggedIn();
/*-----------------------------------------------------------------------------------------
        //Dummy load app Info without verify user login status.


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //progress.dismiss();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                .putExtra("newPsw",false)
                );
            }
        },4000);
-----------------------------------------------------------------------------------------*/
    }

    private void verifyUserLoggedIn(){

        if(tools.getBooleanPreference(Tools.USER_LOOGED_IN_STATUS_KEYNAME)){
            email = tools.getStringPreference(Tools.USER_LOGIN_EMAIL_KEYNAME);
            pass = tools.getStringPreference(Tools.USER_LOGIN_PSW_KEYNAME);
            getUsuario(email,pass);
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //progress.dismiss();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class)
                            .putExtra("newPsw",false)
                    );
                }
            },4000);
        }
    }

    private void getUsuario(final String emailAddress, final String password) {
        //final Usuario[] user = {null};

        //try {
            //Map<String, Object> account = new HashMap<>();
            docRef = mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(emailAddress + "-" + password);
            docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Account cuenta = new Account();
                        if (documentSnapshot.exists()) {
                            mAccount.setNombre(documentSnapshot.getString("nombre"));
                            mAccount.setPassword(documentSnapshot.getString("password"));
                            mAccount.setEmail(documentSnapshot.getString("email"));
                            mAccount.setImg_string(documentSnapshot.getString("img_string"));
                            mAccount.setPregSeguridad(documentSnapshot.getString("pregSeguridad"));
                            mAccount.setResSeguridad(documentSnapshot.getString("resSeguridad"));

                            goToInicio();

                        } else {
                        //message = "Verifica que Contraseña ó User sean correctos.";
                        //showDialogLogin(1, message);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        //progressBar.setVisibility(View.GONE);
                        //message = e.getMessage();
                        //showDialogLogin(1, message);
                    }
                });

        //} catch (Exception e) {
            //e.printStackTrace();
        //}
        //return user[0];
    }

    private void goToInicio(){

        startActivity(new Intent(context, InicioActivity.class)
                .putExtra("email", email)
                .putExtra("psw", pass)
                .putExtra("newPsw",false)
                .putExtra("nombre", mAccount.getNombre())
                .putExtra("pregSeguridad",mAccount.getPregSeguridad())
                .putExtra("resSeguridad",mAccount.getResSeguridad())
        );
    }
}
