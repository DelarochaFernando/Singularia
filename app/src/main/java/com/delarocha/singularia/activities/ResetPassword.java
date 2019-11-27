package com.delarocha.singularia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.annotation.NonNull;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

public class ResetPassword extends AppCompatActivity {

    private EditText email, editUsername, editResSegReset;
    private TextView textMessage;
    private MaterialButton back, resetPswrd;
    private FirebaseAuth auth;
    private FirebaseFirestore mFireStoreDB;
    private DocumentReference docRef;
    private ProgressBar progressBar;
    private Spinner SpinnerPregSegReset;
    private AdapterView.OnItemSelectedListener spinnerListener;
    private Context context = this;
    private String user, pregSeg, resSeg;
    private static String TAG = "RESETPASWORD";
    private TextInputLayout textInputResSegReset;
    //private TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText)findViewById(R.id.email);
        editUsername = (EditText)findViewById(R.id.user);
        textMessage = (TextView)findViewById(R.id.textMessage);
        SpinnerPregSegReset = findViewById(R.id.SpinnerPregSegReset);
        editResSegReset = findViewById(R.id.editResSegReset);
        textInputResSegReset = findViewById(R.id.textInputResSegReset);
        resetPswrd = (MaterialButton)findViewById(R.id.btn_reset_password);
        back = (MaterialButton)findViewById(R.id.btn_back);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        mFireStoreDB = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        user = extras.getString("user");
        back.setVisibility(View.GONE);
        textInputResSegReset.setVisibility(View.VISIBLE);
        if(!user.equals("")){
            textMessage.setText(getResources().getText(R.string.forgot_password_msg_usertyped));
            email.setText(user);
        }else{
            textMessage.setText(getResources().getText(R.string.forgot_password_msg));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class)
                .putExtra("newPsw",false)
                );
            }
        });
        /*ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.security_questions_es));*/

        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions_es,android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerPregSegReset.setAdapter(spAdapter);
        spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //editResSegReset.setVisibility(View.VISIBLE);
                textInputResSegReset.setVisibility(View.VISIBLE);
                pregSeg = (String)adapterView.getItemAtPosition(i);
                if(pregSeg.equals("N/A")){
                    //editResSegReset.setVisibility(View.GONE);
                    textInputResSegReset.setVisibility(View.GONE);
                }else{
                    //editResSegReset.setVisibility(View.VISIBLE);
                    textInputResSegReset.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //editResSegReset.setVisibility(View.GONE);
                textInputResSegReset.setVisibility(View.GONE);
            }
        };
        SpinnerPregSegReset.setOnItemSelectedListener(spinnerListener);
        resSeg = "";

        resetPswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString().trim();
                String userString = editUsername.getText().toString().trim();
                resSeg = editResSegReset.getText().toString();
                /*if (TextUtils.isEmpty(userString)) {
                    Toast.makeText(getApplication(), "Ingresa un Usuario válido.", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(emailString)) {
                    //Toast.makeText(getApplication(), "Ingresa una direccion de correo electrónico válida.", Toast.LENGTH_SHORT).show();
                    email.setError("Correo electrónico inválido.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(pregSeg.equals("N/A")){
                    ((TextView)SpinnerPregSegReset.getSelectedView()).setError("Pregunta Inválida");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(resSeg.equals("")||resSeg.length()==0){
                    editResSegReset.setError("Respuesta Inválida");
                    progressBar.setVisibility(View.GONE);
                    return;
                }


                if(updateResetStatus(emailString, resSeg)){
                    auth.sendPasswordResetEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                AlertDialog.Builder dialog = new AlertDialog.Builder(ResetPassword.this);
                                dialog.setCancelable(false)
                                        .setMessage("Se han enviado instrucciones para reestablecer la contraseña\n" +
                                                "a la cuenta de correo electrónico proporcionada")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                startActivity(new Intent(getApplicationContext(),LoginActivity.class)
                                                        .putExtra("newPsw",true)
                                                );
                                            }
                                        });
                                dialog.show();
                            }else{
                                /*Toast.makeText(ResetPassword.this, "Error al enviar correo de reestablecimiento de contraseña",
                                        Toast.LENGTH_SHORT).show();*/
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(ResetPassword.this);
                                dialog.setCancelable(false)
                                        .setMessage("Error al enviar correo de reestablecimiento de contraseña.\n"+
                                                "Verifique conexión a internet o la cuenta de correo electrónico sea valida")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                dialog.show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }else{
                    progressBar.setVisibility(View.GONE);
                }
                //updateResetStatus(emailString, resSeg);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(ResetPassword.this,LoginActivity.class)
                        .putExtra("newPsw",false)
                );
                break;
        }
        return true;
    }

    private boolean res = false;
    private boolean updateResetStatus(final String email, final String resSeguridad){

        try{

            //docRef = FireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
            mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION)
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      if(task.isSuccessful()){
                         for(QueryDocumentSnapshot document: task.getResult()){
                             if(document.getData().get("resSeguridad").equals(resSeguridad)){
                                 if(document.getId().contains(email)){
                                     final String docID = document.getId();
                                     String[] split = docID.split("-");
                                     String psw = split[1];
                                     docRef = mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION)
                                             .document(email+"-"+psw);
                                     docRef.update("underReset", true)
                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     Log.d(TAG +" "+docID, "DocumentSnapshot successfully updated!");
                                                     res = true;
                                                 }
                                             }).addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Log.d(TAG +" "+docID, "Error updating document", e);
                                         }
                                     });
                                 }
                             }
                         }
                      }
                    }
               });
        }catch (Exception e){
            e.printStackTrace();
            return res;
        }
        return res;
    }
}
