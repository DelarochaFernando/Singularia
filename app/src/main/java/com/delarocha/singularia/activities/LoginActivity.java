package com.delarocha.singularia.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delarocha.singularia.BuildConfig;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.delarocha.singularia.auxclasses.Tools.FIRESTORE_ACCOUNTS_COLLECTION;

//import android.support.annotation.NonNull;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private ImageView imagelogo;
    private EditText editUser, editPsw;
    private ProgressBar progressBar;
    private AppCompatButton btnLogin;
    private LinearLayout layout_recover, linearSignUp;
    private TextView versionApp, txtSignUp;
    private int versionCode = 0;
    private String VERSION_NAME = BuildConfig.VERSION_NAME;
    private Tools tools;

    private Context context = this;
    private boolean newPsw = false;
    private String email, psw, message;
    private Account mAccount;
    private Map<String, Object> account;
    //Start declare Auth
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DocumentReference docRef;
    private FirebaseFirestore mFireStoreDB;
    private MaterialButton btnSignUp, btn_recupera_psw;
    //End declare Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tools = new Tools(context);
        Bundle extras = getIntent().getExtras();
        newPsw = extras.getBoolean("newPsw");

        imagelogo = (ImageView) findViewById(R.id.imageLogo);
        editPsw = (EditText) findViewById(R.id.editPsw);
        editUser = (EditText) findViewById(R.id.editUser);
        editUser.setText("ferchaparro11@hotmail.com");
        editPsw.setText("pablo101");
        //editUser.setText("");
        //editPsw.setText("");
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnLogin = (AppCompatButton) findViewById(R.id.btnLogin);
        btn_recupera_psw = (MaterialButton)findViewById(R.id.btn_recupera_psw);
        btnSignUp = (MaterialButton)findViewById(R.id.btnSignUp);
        layout_recover = (LinearLayout) findViewById(R.id.layout_recover);
        versionApp = (TextView) findViewById(R.id.textVersionApp);
        //txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        linearSignUp = (LinearLayout) findViewById(R.id.linearSignUp);
        versionApp.setText("version: " + VERSION_NAME);
        //btn_recupera_psw.setTe

        mAuth = FirebaseAuth.getInstance();
        //mUser = mAuth.getCurrentUser();
        mFireStoreDB = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                mAccount = new Account();
                email = editUser.getText().toString();
                psw = editPsw.getText().toString();

                    if (validarInfo(email, psw)) {
                        if(requestPermsforApp(LoginActivity.this)){
                            userLogin(email,psw);
                        }else{
                            progressBar.setVisibility(View.GONE);
                        }
                    }else{
                        progressBar.setVisibility(View.GONE);
                    }

            }
        });

        /*layout_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPassword.class)
                    .putExtra("user",editUser.getText().toString())
                );
            }
        });*/

        btn_recupera_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPassword.class)
                    .putExtra("user",editUser.getText().toString())
                );
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SignUpActivity.class));
            }
        });

        /*linearSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SignUpActivity.class));
            }
        });
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SignUpActivity.class));
            }
        });*/

    }

    private void showDialogLogin(int opt, String message) {
        if (opt == 0) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Bienvenido.")
                    .setIcon(R.drawable.ic_green_check)
                    .setMessage("\n\t\t"+message)
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBar.setVisibility(View.GONE);
                            //userLogin(email, psw);
                            tools.setBooleanPreference(Tools.USER_LOOGED_IN_STATUS_KEYNAME,true);
                            tools.setStringPreference(Tools.USER_LOGIN_EMAIL_KEYNAME,email);
                            tools.setStringPreference(Tools.USER_LOGIN_PSW_KEYNAME,psw);
                            startActivity(new Intent(context, InicioActivity.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre", mAccount.getNombre())
                                .putExtra("pregSeguridad",mAccount.getPregSeguridad())
                                .putExtra("resSeguridad",mAccount.getResSeguridad())
                            );
                            dialogInterface.dismiss();
                        }
                    }).create();
            dialog.show();
        } else if (opt == 1) {
            //String message = e.getMessage();
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Error.")
                    .setIcon(R.drawable.ic_red_error)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBar.setVisibility(View.GONE);
                            dialogInterface.dismiss();
                        }
                    }).show();
            dialog.show();
        }
    }

    private boolean validarInfo(String email, String psw) {

        boolean res = true;

        if (email.length() == 0) {
            editUser.setError("Email requerido.");
            res = false;
        }
        if (psw.length() == 0) {
            editPsw.setError("Password requerido.");
            res = false;
        }
        Matcher pEmail = Patterns.EMAIL_ADDRESS.matcher(email);
        if (!pEmail.matches()) {
            editUser.setError("No es email valido.");
            res = false;
        }
        if (psw.length() > 8) {
            editPsw.setError("Contraseña debe ser No mayor de 8 caracteres.");
            res = false;
        }

        return res;
    }

    private void userLogin(final String email, final String psw) {
        //progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                //getUsuario(email,psw);
                if (task.isSuccessful()) {

                    mUser = mAuth.getCurrentUser();
                    if(newPsw){
                        getUserFromAuth(mUser, psw);
                    }else{
                        getUsuario(email,psw);
                    }
                    /*startActivity(new Intent(context, InicioActivity.class)
                            .putExtra("email", email)
                            .putExtra("psw", psw)
                            .putExtra("nombre", mAccount.getNombre())
                    );*/
                    //getUsuario(email,psw);
                    /*AlertDialog dialog = new AlertDialog.Builder(context)
                            //+ usuario.getNombre() +" "+usuario.getLastname()
                            .setTitle("Acceso permitido.")
                            .setIcon(R.drawable.ic_green_check)
                            .setMessage("Acceso permitido. ")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    startActivity(new Intent(context, InicioActivity.class).
                                            putExtra("email",email).putExtra("psw",psw));
                                    dialogInterface.dismiss();
                                }
                            }).create();
                    dialog.show();*/
                } else {
                    String message = task.getException().getMessage();
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Error.")
                            .setIcon(R.drawable.ic_red_error)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressBar.setVisibility(View.GONE);
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }
        });
    }

    private void getUserFromAuth(FirebaseUser mUser, final String psw){

        try {
            final String email = mUser.getEmail();
            mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){

                                    if((boolean)document.getData().get("underReset")){
                                        //If the account's password was recently modified
                                        //1. the current account will be deleted
                                        //2. the account will be re-created with new Id (email-passwordUpdated)
                                        final Account account = new Account(
                                                document.getString("nombre"),
                                                document.getString("email"),
                                                document.getString("img_string"),
                                                document.getString("img_storage_path"),
                                                document.getString("password"),
                                                document.getString("pregSeguridad"),
                                                document.getString("resSeguridad"),
                                                document.getBoolean("underReset")
                                        );

                                        mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION)
                                          .document(email+"-"+account.getPassword())
                                          .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.i("DELETE","Account "+email+"-"+account.getPassword()+" was successfull deleted.");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i("ERROR","Account "+email+"-"+account.getPassword()+" error on delete");
                                                    }
                                                });
                                        
                                        Map<String, Object> mapAccount = new HashMap<String, Object>();
                                        mapAccount.put("img_string",account.getImg_string());
                                        mapAccount.put("nombre",account.getNombre());
                                        mapAccount.put("email",account.getEmail());
                                        mapAccount.put("password",psw);//new Password
                                        mapAccount.put("pregSeguridad",account.getPregSeguridad());
                                        mapAccount.put("resSeguridad",account.getResSeguridad());
                                        mapAccount.put("underReset",false);

                                        mFireStoreDB.collection(FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw)
                                                .set(mapAccount)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void avoid) {
                                                        Log.i("ACCOUNT RE-CREATED","Account "+email+"-"+psw+" was successfull created.");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("ERROR","Account "+email+"-"+psw+" error on re-create the Account");
                                            }
                                        });

                                        docRef = mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email + "-" + psw);
                                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Account cuenta = new Account();
                                                if (documentSnapshot.exists()) {
                                                    mAccount.setNombre(documentSnapshot.getString("nombre"));
                                                    mAccount.setPassword(documentSnapshot.getString("password"));
                                                    mAccount.setEmail(documentSnapshot.getString("email"));
                                                    mAccount.setImg_string(documentSnapshot.getString("img_string"));
                                                    mAccount.setUnderReset(documentSnapshot.getBoolean("underReset"));
                                                    message = mAccount.getNombre();
                                                    showDialogLogin(0, message);
                                                } else {
                                                    message = "Verifica que Contraseña ó User sean correctos.";
                                                    showDialogLogin(1, message);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                message = e.getMessage();
                                                showDialogLogin(1, message);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUsuario(final String emailAddress, final String password) {
        //final Usuario[] user = {null};

        try {
            //Map<String, Object> account = new HashMap<>();
            docRef = mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(emailAddress + "-" + password);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                        message = mAccount.getNombre();
                        showDialogLogin(0, message);
                    } else {
                        message = "Verifica que Contraseña ó User sean correctos.";
                        showDialogLogin(1, message);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    message = e.getMessage();
                    showDialogLogin(1, message);
                }
            });
            /*docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(task.isSuccessful()){
                        if(document.exists()){
                            account = document.getData();
                            usuario.setImg_string(account.get("img_string").toString());
                            usuario.setNombre(account.get("firstname").toString());
                            usuario.setLastname(account.get("lastname").toString());
                            usuario.setSurname(account.get("surname").toString());
                            usuario.setEmail(account.get("email").toString());
                            usuario.setPassword(account.get("password").toString());
                        }else {

                        }
                    }else {

                    }
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return user[0];
    }

    private boolean requestPermsforApp(Activity loginActivity) {
        boolean  res = false;
        try {
            if (
                    ContextCompat.checkSelfPermission(loginActivity,
                            Manifest.permission.INTERNET )!= PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(loginActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(loginActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(loginActivity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(loginActivity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(loginActivity,
                                    Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(loginActivity,
                                    Manifest.permission.READ_PHONE_STATE)
                                    != PackageManager.PERMISSION_GRANTED
            ) {

                // Should we show an explanation?
                if (
                        ActivityCompat.shouldShowRequestPermissionRationale(loginActivity,
                                Manifest.permission.INTERNET)||
                                ActivityCompat.shouldShowRequestPermissionRationale(loginActivity,
                                        Manifest.permission.ACCESS_COARSE_LOCATION)||
                                ActivityCompat.shouldShowRequestPermissionRationale(loginActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION)||
                                ActivityCompat.shouldShowRequestPermissionRationale(loginActivity,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)||
                                ActivityCompat.shouldShowRequestPermissionRationale(loginActivity,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                                ActivityCompat.shouldShowRequestPermissionRationale(loginActivity,
                                        Manifest.permission.CAMERA)||
                                ActivityCompat.shouldShowRequestPermissionRationale(loginActivity,
                                        Manifest.permission.READ_PHONE_STATE)
                ) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    builder.setMessage("Éste permiso es importante para la funcionalidad de la Aplicación.")
                            .setTitle("Permiso Importante Requerido")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.INTERNET},1);
                                    ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                                    ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                                    ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                                    ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                                    ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.CAMERA},1);
                                    ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
                                }
                            }).show();
                    res = true;
                } else {
                    // No explanation needed, we can request the permission.
//                    ActivityCompat.requestPermissions(loginActivity,
//                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},COARSE_LOCATION);
//                    ActivityCompat.requestPermissions(loginActivity,
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION);
                    ActivityCompat.requestPermissions(loginActivity,
                            new String[]{Manifest.permission.INTERNET,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_PHONE_STATE},1);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else {
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean res = false;
        switch (requestCode) {
            case 1:
                if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)&&permissions[0]==Manifest.permission.INTERNET) {

                    res = true;
                }
                if ((grantResults[1] == PackageManager.PERMISSION_GRANTED)&&permissions[0]==Manifest.permission.ACCESS_COARSE_LOCATION) {

                    res = true;
                }
                if((grantResults[2] == PackageManager.PERMISSION_GRANTED)&&permissions[1]==Manifest.permission.ACCESS_FINE_LOCATION){
                    res = true;
                }
                if((grantResults[3] == PackageManager.PERMISSION_GRANTED)&&permissions[2]==Manifest.permission.READ_EXTERNAL_STORAGE){
                    res = true;
                }
                if((grantResults[4] == PackageManager.PERMISSION_GRANTED)&&permissions[3]==Manifest.permission.WRITE_EXTERNAL_STORAGE){
                    res = true;
                }
                if((grantResults[5] == PackageManager.PERMISSION_GRANTED)&&permissions[4]==Manifest.permission.CAMERA){
                    res = true;
                }
                if((grantResults[6] == PackageManager.PERMISSION_GRANTED)&&permissions[5]==Manifest.permission.READ_PHONE_STATE){
                    res = true;
                }
                break;
        }
        if(res){

            /*if (validarInfo(email, psw)) {
                userLogin(email,psw);
            }else{
                progressBar.setVisibility(View.GONE);
            }*/
            //validarInfo(email,psw);
            userLogin(email, psw);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean choice = true;
        if (keyCode == event.KEYCODE_BACK) {
            choice = false;
        }
        return choice;
    }
}
