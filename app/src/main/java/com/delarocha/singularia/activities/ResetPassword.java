package com.delarocha.singularia.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.delarocha.singularia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText email;
    private Button back, resetPswrd;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        email = (EditText)findViewById(R.id.email);
        resetPswrd = (Button)findViewById(R.id.btn_reset_password);
        back = (Button)findViewById(R.id.btn_back);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        resetPswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString().trim();
                if (TextUtils.isEmpty(emailString)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            /*Toast.makeText(ResetPassword.this,
                                    "Se han enviado instrucciones para reestablecer la contraseña"+
                                            " a la cuenta de correo proporcionada", Toast.LENGTH_SHORT).show();*/
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ResetPassword.this);
                            dialog.setCancelable(false)
                            .setMessage("Se han enviado instrucciones para reestablecer la contraseña\n" +
                                    "a la cuenta de correo electrónico proporcionada")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                }
                            });
                            dialog.show();
                        }else{
                            Toast.makeText(ResetPassword.this, "Error al enviar correo de reestablecimiento de contraseña",
                                    Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder dialog = new AlertDialog.Builder(ResetPassword.this);
                            dialog.setCancelable(false)
                                    .setMessage("Error al enviar correo de reestablecimiento de contraseña.\n"+
                                            "Verifique conexión a internet o la cuenta de correo electrónico sea valida")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            dialog.show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}
