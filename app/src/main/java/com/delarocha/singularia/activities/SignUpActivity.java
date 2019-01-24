package com.delarocha.singularia.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private EditText editNombre, editEmail, editPsw;
    private Button btnCreateAcc;
    private CircleImageView circleImageView;
    private TextView txtPicture;
    private ProgressBar progressBar;
    private Context context = this;
    private Activity signActivity = this;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDB;

    private static int CAMERA_REQ_CODE = 1;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private int CHOOSE_PIC_CODE = 2;
    private int fotoElegida;
    private boolean createdSuccessfully = false;
    private Bitmap bmToSave;
    private String img_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle("Create your Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        circleImageView = (CircleImageView)findViewById(R.id.imgProfile);
        txtPicture = (TextView)findViewById(R.id.txtPicture);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        editNombre = (EditText)findViewById(R.id.editNombre);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPsw = (EditText)findViewById(R.id.editPsw);
        btnCreateAcc = (Button)findViewById(R.id.btnCreateAcc);

        mAuth = FirebaseAuth.getInstance();
        mFireStoreDB = FirebaseFirestore.getInstance();

        circleImageView.setBorderColor(getResources().getColor(R.color.colorWhite) );
        circleImageView.setBackground(getResources().getDrawable(R.drawable.ic_default_user));
        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editNombre.getText().toString();
                String email = editEmail.getText().toString();
                String psw = editPsw.getText().toString();
                if(validarInfo(name,email,psw)){
                    createAccount(name,email,psw);
                }

            }
        });
        txtPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(requestPermsforApp(signActivity)){
                    takePictureIntent();
                }
            }
        });

    }



    private boolean validarInfo(String name,String email, String psw){

        boolean res = true;

        if(name.length()==0){
            editNombre.setError("Nombre requerido.");
        }
        if (email.length() == 0) {
            editEmail.setError("Email requerido.");
            res = false;
        }
        if(psw.length()==0){
            editPsw.setError("Password requerido.");
            res = false;
        }
        Matcher pEmail =  Patterns.EMAIL_ADDRESS.matcher(email);
        if(!pEmail.matches()){
            editEmail.setError("No es email valido.");
            res = false;
        }
        if(psw.length()>8){
            editPsw.setError("Contraseña debe ser No mayor de 8 caracteres.");
            res = false;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmToSave.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte[] data_image = stream.toByteArray();
        img_str = Base64.encodeToString(data_image, Base64.DEFAULT);
        if(data_image!=null){
            data_image = null;
        }
        return res;
    }

    private void createAccount(final String nombre,final String emailAddress, final String password){

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            saveIntoDB(nombre,emailAddress,password);
                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("Proceso completado.")
                                    .setIcon(R.drawable.ic_green_check)
                                    .setMessage("Usuario creado Satisfactoriamente.")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(context,LoginActivity.class));
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                            dialog.show();
                        }else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                AlertDialog dialog = new AlertDialog.Builder(context)
                                        .setTitle("Error.")
                                        .setIcon(R.drawable.ic_red_error)
                                        .setMessage("El Usuario ya ha sido registrado.")
                                        .setCancelable(false)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).show();
                                dialog.show();
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
                    }
                });
    }

    private void saveIntoDB(String name ,String emailAddress, String password) {

        try{

            Map<String, Object> account = new HashMap<>();
            //String[] splitName = name.split(" ");
            //String nombre = splitName[0];
            String nombre = name;
            //String lastname = splitName[1];
            //String surname = splitName[2];

            account.put("img_string",img_str);
            account.put("nombre",nombre);
            //account.put("lastname", lastname);
            //account.put("surname", surname);
            account.put("email",emailAddress);
            account.put("password",password);
            mFireStoreDB.collection("accounts").document(emailAddress+"-"+password)
                    .set(account)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void avoid) {
                    createdSuccessfully = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    createdSuccessfully = false;
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                AlertDialog dialogBack = new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_alert)
                        .setTitle("Atención")
                        .setMessage("¿Deseas terminar la creación de la cuenta?")
                        .setMessage("La información se perderá.")
                        .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                break;
                default:
                    return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void takePictureIntent() {

        AlertDialog.Builder dialogProfPic = new AlertDialog.Builder(this);
        dialogProfPic.setTitle("Picture");
        dialogProfPic.setPositiveButton("From Photos", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                //CHOOSE_PIC_CODE = 2;
                startActivityForResult(intent, CHOOSE_PIC_CODE);
            }
        });
        dialogProfPic.setNegativeButton("From Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    //REQUEST_IMAGE_CAPTURE; = 1;
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        AlertDialog dialog = dialogProfPic.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                //dialog = ProgressDialog.show(Formulario.this, "Fotografia", "Procesando...", true, false);
                fotoElegida = 1;
                Bundle extras = data.getExtras();
                bm = (Bitmap) extras.get("data");
                bmToSave = bm;
                circleImageView.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CHOOSE_PIC_CODE && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            try {
                fotoElegida = 2;
                bm = imageFromGallery(imgUri);
                bmToSave = bm;
                circleImageView.setImageBitmap(bm);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Bitmap imageFromGallery(Uri uri){
        boolean landscape = false;
        boolean portrait = false;
        try{
            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ExifInterface exif = new ExifInterface(uri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            /*if(orientation<=0){
                orientation
            }*/
            int angle = 0;
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                default:
                    angle = 0;
                    break;
            }
            if(bm.getWidth()>bm.getHeight()) {
                landscape = true;
            }else{
                portrait = true;
            }
            //boolean landscape = bm.getWidth()>bm.getHeight();
            //boolean portrait = bm.getHeight()>bm.getWidth();
            Matrix m = new Matrix();
            if(angle == 0&& landscape){
                m.postRotate(270);
            }else if(angle == 0 && portrait) {
                m.postRotate(angle);
            }

            return Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),m,true);

        }catch(IOException e){
            Log.e("", "-- Error in setting image");
        }catch (OutOfMemoryError oom){
            Log.e("", "-- OOM Error in setting image");
        }
        return null;
    }

    private boolean requestPermsforApp(Activity signActivity) {
        boolean  res = false;
        try {
            if (ContextCompat.checkSelfPermission(signActivity,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(signActivity,
                        Manifest.permission.CAMERA)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Éste permiso es importante para la captura de la identidad del Usuario.")
                            .setTitle("Permiso Importante Requerido")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQ_CODE);
                                }
                            }).show();
                    res = true;
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(signActivity,
                            new String[]{Manifest.permission.CAMERA},CAMERA_REQ_CODE);
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
        switch (requestCode){
            case 1:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

                }else if(grantResults[0]== PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(signActivity,
                            new String[]{Manifest.permission.CAMERA},CAMERA_REQ_CODE);
                }
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
