package com.delarocha.singularia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.regex.Matcher;

public class LoginActivity extends AppCompatActivity {

    private ImageView imagelogo;
    private EditText editUser, editPsw;
    private ProgressBar progressBar;
    private Button btnLogin;
    private LinearLayout layout_recover, linearSignUp;
    private TextView versionApp, txtSignUp;
    private int versionCode = 0;
    private String VERSION_NAME = BuildConfig.VERSION_NAME;

    private Context context = this;
    private String email, psw, message;
    private Account mAccount;
    private Map<String, Object> account;
    //Start declare Auth
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DocumentReference docRef;
    private FirebaseFirestore mFireStoreDB;
    //End declare Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imagelogo = (ImageView)findViewById(R.id.imageLogo);
        editPsw = (EditText)findViewById(R.id.editPsw);
        editUser = (EditText)findViewById(R.id.editUser);
        editUser.setText("fernando.delarocha88@gmail.com");
        editPsw.setText("fer123");
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        layout_recover = (LinearLayout)findViewById(R.id.layout_recover);
        versionApp = (TextView)findViewById(R.id.textVersionApp);
        txtSignUp = (TextView)findViewById(R.id.txtSignUp);
        linearSignUp = (LinearLayout) findViewById(R.id.linearSignUp);
        versionApp.setText("version: "+VERSION_NAME);

        mAuth = FirebaseAuth.getInstance();
        mUser  = mAuth.getCurrentUser();
        mFireStoreDB = FirebaseFirestore.getInstance();
        //docRef.


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                mAccount = new Account();
                email = editUser.getText().toString();
                psw = editPsw.getText().toString();
                //getUsuario(email, psw);
                if(validarInfo(email,psw)){
                    getUsuario(email, psw);
                    //userLogin(email, psw);
                }
            }
        });

        layout_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPassword.class));
            }
        });

        linearSignUp.setOnClickListener(new View.OnClickListener() {
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
        });

    }

    private void showDialogLogin(int opt, String message) {
        if(opt ==0){
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Bienvenido.")
                    .setIcon(R.drawable.ic_green_check)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBar.setVisibility(View.GONE);
                            userLogin(email, psw);
                            dialogInterface.dismiss();
                        }
                    }).create();
            dialog.show();
        }else if(opt==1){
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

    private boolean validarInfo(String email, String psw){

        boolean res = true;

        if (email.length() == 0) {
            editUser.setError("Email requerido.");
            res = false;
        }
        if(psw.length()==0){
            editPsw.setError("Password requerido.");
            res = false;
        }
        Matcher pEmail =  Patterns.EMAIL_ADDRESS.matcher(email);
        if(!pEmail.matches()){
            editUser.setError("No es email valido.");
            res = false;
        }
        if(psw.length()>8){
            editPsw.setError("Contraseña debe ser No mayor de 8 caracteres.");
            res = false;
        }

        return res;
    }

    private void userLogin(final String email, final String psw){
        //progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                 //getUsuario(email,psw);
                if(task.isSuccessful()){
                    startActivity(new Intent(context, InicioActivity.class)
                            .putExtra("email",email)
                            .putExtra("psw",psw)
                            .putExtra("nombre", mAccount.getNombre())
                    );
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
                }else{
                    String message = task.getException().getMessage();
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Error.")
                            .setIcon(R.drawable.ic_red_error)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }
        });


    }

    private void getUsuario(final String emailAddress, final String password){
        //final Usuario[] user = {null};

        try{
            //Map<String, Object> account = new HashMap<>();
            docRef = mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Account cuenta = new Account();
                    //user[0].setNombre(documentSnapshot.getString("nombre"));
                    //user[0].setPassword(documentSnapshot.getString("password"));
                    if(documentSnapshot.exists()){
                        mAccount.setNombre(documentSnapshot.getString("nombre"));
                        //mAccount.setLastname(documentSnapshot.getString("lastname"));
                        //mAccount.setSurname(documentSnapshot.getString("surname"));
                        mAccount.setPassword(documentSnapshot.getString("password"));
                        mAccount.setEmail(documentSnapshot.getString("email"));
                        mAccount.setImg_string(documentSnapshot.getString("img_string"));
                       message = mAccount.getNombre();
                       showDialogLogin(0,message);
                    }else{
                        message = "Verifica que Contraseña ó User sean correctos.";
                        showDialogLogin(1,message);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    message = e.getMessage();
                    showDialogLogin(1,message);
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
        }catch (Exception e){
            e.printStackTrace();
        }
        //return user[0];
    }
    /*public void goRecoverPassword(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean choice = true;
        if(keyCode == event.KEYCODE_BACK){
            choice = false;
        }
        return choice;
    }
}
