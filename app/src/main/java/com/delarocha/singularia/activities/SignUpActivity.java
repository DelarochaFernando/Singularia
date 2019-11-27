package com.delarocha.singularia.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
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
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.delarocha.singularia.auxclasses.Tools.FIRESTORE_ACCOUNTS_COLLECTION;

//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    //private EditText editNombre, editEmail, editPsw, editResSeg;
    private TextInputEditText editNombre, editEmail, editPsw, editResSeg;
    //private Button btnCreateAcc;
    private CircleImageView circleImageView;
    private TextView txtPicture;
    private Spinner pregSegSpinner;
    private ProgressBar progressBar;
    private Context context = this;
    private Activity signActivity = this;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDB;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;

    private static int CAMERA_REQ_CODE = 1;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private int CHOOSE_PIC_CODE = 2;
    private int fotoElegida;
    private boolean createdSuccessfully = false;
    private AdapterView.OnItemSelectedListener spinnerListener;
    private boolean conFotoPerfil = false;
    private Bitmap bmToSave;
    private byte[] data_image;
    private String img_str,name,email,psw, pregSeg, resSeg;
    private TextInputLayout inputlayoutResSeg;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle("Create your Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        circleImageView = (CircleImageView)findViewById(R.id.imgProfile);
        txtPicture = (TextView)findViewById(R.id.txtPicture);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        //editNombre = (EditText)findViewById(R.id.editNombre);
        //editEmail = (EditText)findViewById(R.id.editEmail);
        //editPsw = (EditText)findViewById(R.id.editPsw);
        //editResSeg = (EditText)findViewById(R.id.editResSeg);
        editNombre = (TextInputEditText) findViewById(R.id.editNombre);
        editEmail = (TextInputEditText)findViewById(R.id.editEmail);
        editPsw = (TextInputEditText)findViewById(R.id.editPsw);
        pregSegSpinner = findViewById(R.id.SpinnerPregSeg);
        inputlayoutResSeg = findViewById(R.id.inputlayoutResSeg);
        editResSeg = (TextInputEditText)findViewById(R.id.editResSeg);
        //btnCreateAcc = (Button)findViewById(R.id.btnCreateAcc);

        tools = new Tools(context);
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDB = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        circleImageView.setBorderColor(getResources().getColor(R.color.colorWhite) );
        circleImageView.setBackground(getResources().getDrawable(R.drawable.ic_default_user_white));
        resSeg = "";
        pregSeg = "";
        name = editNombre.getText().toString();
        email = editEmail.getText().toString();
        psw = editPsw.getText().toString();
        resSeg = editResSeg.getText().toString();

        /*btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String name = editNombre.getText().toString();
                //String email = editEmail.getText().toString();
                //String psw = editPsw.getText().toString();
                //resSeg = editResSeg.getText().toString();

                if(validarInfo(name,email,psw)){
                    createAccount(name,email,psw);
                }
            }
        });*/
        txtPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(requestPermsforApp(signActivity)){
                    takePictureIntent();
                }
            }
        });

        /*ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.security_questions_es));*/

        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions_es,R.layout.smallertext_spinner);
        //spAdapter.setDropDownViewResource(R.layout.smallertext_spinner);
        pregSegSpinner.setAdapter(spAdapter);
        spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pregSeg = (String)adapterView.getItemAtPosition(i);
                if(pregSeg.equals("N/A")){
                    editResSeg.setVisibility(View.GONE);
                }else{
                    editResSeg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //pregSegSpinner.setSelection(adapterView.getFirstVisiblePosition());
                editResSeg.setVisibility(View.GONE);
            }
        };
        pregSegSpinner.setOnItemSelectedListener(spinnerListener);
    }



    private boolean validarInfo(String name,String email, String psw){

        boolean res = true;

        if(name.equals("")||name.length()<5){
            editNombre.setError("Nombre requerido.");
            //res = false;
            return false;
        }
        if (email.equals("")) {
            editEmail.setError("Email requerido.");
            //res = false;
            return false;
        }
        if(psw.equals("")||psw.length()==0){
            editPsw.setError("Password requerido.");
            //res = false;
            return false;
        }
        Matcher pEmail =  Patterns.EMAIL_ADDRESS.matcher(email);
        if(!pEmail.matches()){
            editEmail.setError("No es email valido.");
            //res = false;
            return false;
        }
        if(psw.length()>8){
            editPsw.setError("Contraseña debe ser No mayor de 8 caracteres.");
            //res = false;
            return false;
        }

        if(pregSegSpinner.getSelectedItem().toString().equals("N/A")){
            ((TextView)pregSegSpinner.getSelectedView()).setError("Pregunta de Seguridad requerida");
            //res = false;
            return false;
        }

        if(resSeg.equals("")||resSeg.length()==0){
            editResSeg.setError("Respuesta de Seguridad requerida");
            //res = false;
            return false;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(conFotoPerfil){
            bmToSave.compress(Bitmap.CompressFormat.JPEG,70,stream);
            data_image = stream.toByteArray();
            //img_str = Base64.encodeToString(data_image, Base64.DEFAULT);
            /*if(data_image!=null){
                data_image = null;
            }*/
        }else{
            //Usuario sin foto de perfil.
            data_image = null;
            //img_str = "";
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

                                uploadImageToFireStorage(data_image);

                            //saveIntoDB(nombre,emailAddress,password);

                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("Proceso completado.")
                                    .setIcon(R.drawable.ic_green_check)
                                    .setMessage("Usuario creado Satisfactoriamente.")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(context,LoginActivity.class)
                                                .putExtra("newPsw", false)
                                            );
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

    private void saveIntoDB(String name ,String emailAddress, String password, Uri uri) {

//        try{

            Map<String, Object> account = new HashMap<>();
            String nombre = name;
            //account.put("img_string",img_str);
            if(uri!=null){
                account.put("img_storage_path",uri.toString());
            }else{
                account.put("img_storage_path","");
            }
            //account.put("img_storage_path",uri.toString());
            account.put("img_string","");
            account.put("nombre",nombre);
            account.put("email",emailAddress);
            account.put("password",password);
            account.put("pregSeguridad", pregSeg);
            account.put("resSeguridad",resSeg);
            account.put("underReset",false);
            mFireStoreDB.collection(FIRESTORE_ACCOUNTS_COLLECTION)
                    .document(emailAddress+"-"+password)
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
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void uploadImageToFireStorage(byte[] byteArrayImage){


        String fecha = tools.getFecha();
        final Uri uri = null;
        if(byteArrayImage!=null){
            InputStream byteArrayIS = new ByteArrayInputStream(byteArrayImage);
            mStorageRef = mFirebaseStorage.getReference();
            StorageReference carpeta = mStorageRef.child("singulariaUserProfilePic/");
            //StorageReference imagen = carpeta.child(Calendar.getInstance().getTimeInMillis()+"|"+fecha+".jpg");
            StorageReference imagen  = carpeta.child(mAuth.getUid()+".jpg");
            //final Task<Uri>[] imgURL = new Task<Uri>[1];
            //UploadTask uploadTask = imagen.putBytes(byteArrayImage);
            UploadTask uploadTask = imagen.putStream(byteArrayIS);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    saveIntoDB(name,email,psw,uri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            e.printStackTrace();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }else{
            saveIntoDB(name,email,psw,uri);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_signup_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                if(!name.equals("")||!email.equals("")||!psw.equals("")){
                    AlertDialog dialogBack = new AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_alert)
                            .setTitle("Atención")
                            .setMessage("¿Deseas terminar la creación de la cuenta?")
                            .setMessage("La información se perderá.")
                            .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class)
                                        .putExtra("newPsw",false)
                                    );
                                }
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }else {
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class)
                        .putExtra("newPsw",false)
                    );
                }

                break;
            case R.id.btnCreateAcc:
                name = editNombre.getText().toString();
                email = editEmail.getText().toString();
                psw = editPsw.getText().toString();
                resSeg = editResSeg.getText().toString();
                if(validarInfo(name,email,psw)){
                    createAccount(name,email,psw);
                }
                break;
                default:
                    return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void takePictureIntent() {

        AlertDialog.Builder dialogProfPic = new AlertDialog.Builder(this);
        dialogProfPic.setTitle("Picture");
        dialogProfPic.setPositiveButton("De Galería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                //CHOOSE_PIC_CODE = 2;
                startActivityForResult(intent, CHOOSE_PIC_CODE);
            }
        });
        dialogProfPic.setNegativeButton("De Cámara", new DialogInterface.OnClickListener() {
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
                Glide.with(context).load(bm).into(circleImageView);
                //circleImageView.setImageBitmap(bm);
                conFotoPerfil = true;
            } catch (Exception e) {
                conFotoPerfil = false;
                e.printStackTrace();
            }
        }

        if (requestCode == CHOOSE_PIC_CODE && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            try {
                fotoElegida = 2;
                bm = imageFromGallery(imgUri);
                bmToSave = bm;
                Glide.with(context).load(bm).into(circleImageView);
                //circleImageView.setImageBitmap(bm);
                conFotoPerfil = true;
            }catch (Exception e){
                conFotoPerfil = false;
                e.printStackTrace();
            }
        }
    }

    private Bitmap imageFromGallery(Uri uri){
        boolean landscape = false;
        boolean portrait = false;
        try{

            ContentResolver resolver = this.getContentResolver();
            Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, uri);
            InputStream inputStream = resolver.openInputStream(uri);
            ExifInterface exif = new ExifInterface(inputStream);
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
            if (
                    ContextCompat.checkSelfPermission(signActivity,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ||
                        ContextCompat.checkSelfPermission(signActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                    ||
                        ContextCompat.checkSelfPermission(signActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            !=PackageManager.PERMISSION_GRANTED
            ) {

                // Should we show an explanation?
                if (
                        ActivityCompat.shouldShowRequestPermissionRationale(signActivity,
                        Manifest.permission.CAMERA)||
                        ActivityCompat.shouldShowRequestPermissionRationale(signActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                        ActivityCompat.shouldShowRequestPermissionRationale(signActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Éste permiso es importante para la captura de la identidad del Usuario.")
                            .setTitle("Permiso Importante Requerido")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQ_CODE);
                                    ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                                    ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                                }
                            }).show();
                    res = true;
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(signActivity,
                            new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE},1);
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
