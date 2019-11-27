package com.delarocha.singularia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountConfiguration extends AppCompatActivity {


    private static int CAMERA_REQ_CODE = 1;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private int CHOOSE_PIC_CODE = 2;
    private int fotoElegida;
    private FirebaseFirestore mFireStoreDB;
    private DocumentReference dRef;
    private String username, email, psw,pswFromIntent, avatar_img_str,avatar_storage_path, preguntaDeSeg, resPregunta;
    private String nom, mail, pass, respuestaSeg;
    private Context context = this;
    private CircleImageView imgProfile;
    private TextInputEditText editNombre, editEmail, editResSeg, editPswActual, editPswNew, editPswNewConfirm;
    private LinearLayout linearChangePsw, linearChangePregSeg;
    private Spinner spinnerPregSeg;
    private AdapterView.OnItemSelectedListener spinnerListener;
    private byte[] decodedByteArr;
    private boolean fromTipoInvita = false;
    private boolean allInfoSaved = false;
    private Bitmap bmToSave;
    private MaterialButton txtPicture, btnChangePsw,btnChangePregSeg, btnCancel,btnCancelPreg, btnUpdatePsw, btnSavePreg;
    private boolean conFotoPerfil;
    private String pregSeg, contraseñaFromDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressBar progressBar;
    private int pregPos = 0;
    private boolean changingPregSeg = false;
    private boolean changingPassword = false;
    private Account account;
    private Tools tools;
    private StorageReference mStorageRef;
    private FirebaseStorage mFirebaseStorage;
    private byte[] data_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_configuration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Configuración de Cuenta");
        Bundle extras = getIntent().getExtras();

        username = extras.getString("nombre");
        email = extras.getString("email");
        pswFromIntent = extras.getString("psw");
        //avatar_img_str = extras.getString("img_str");
        preguntaDeSeg = extras.getString("pregSeguridad");
        pregSeg = "";
        resPregunta = extras.getString("resSeguridad");
        fromTipoInvita = extras.getBoolean(Tools.FROM_TIPOINVITA_TAG);

        tools = new Tools(context);
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDB = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        imgProfile = findViewById(R.id.imgProfile);
        txtPicture = findViewById(R.id.txtPicture);
        editNombre = findViewById(R.id.editNombre);
        editEmail = findViewById(R.id.editEmail);
        btnChangePsw = findViewById(R.id.btnChangePsw);
        btnChangePregSeg = findViewById(R.id.btnChangePregSeg);
        linearChangePsw = findViewById(R.id.linearChangePsw);
        linearChangePregSeg = findViewById(R.id.linearChangePregSeg);
        editPswActual = findViewById(R.id.editPswActual);
        editPswNew = findViewById(R.id.editPswNew);
        editPswNewConfirm = findViewById(R.id.editPswNewConfirm);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancelPreg = findViewById(R.id.btnCancelPreg);
        btnUpdatePsw = findViewById(R.id.btnUpdatePsw);
        btnSavePreg = findViewById(R.id.btnSavePreg);
        editResSeg = findViewById(R.id.editResSeg);
        spinnerPregSeg = findViewById(R.id.SpinnerPregSeg);
        progressBar = findViewById(R.id.progressbar);

        getUsuario2(email,pswFromIntent);

        /*if(!avatar_img_str.equals("")){
            decodedByteArr = Base64.decode(avatar_img_str, Base64.DEFAULT);
            Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedByteArr, 0, decodedByteArr.length);
            Glide.with(context).load(userBitmap).into(imgProfile);
            //image_User.setImageBitmap(userBitmap);
        }else{
            imgProfile.setBackground(getResources().getDrawable(R.drawable.ic_default_user_coloraccent));
        }*/

        //editNombre.setText(username);
        //editEmail.setText(email);
        //editPsw.setText(psw);

        txtPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureIntent();
            }
        });

        btnChangePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changingPassword = true;
                btnChangePsw.setEnabled(false);
                linearChangePsw.setVisibility(View.VISIBLE);
                editPswNew.setEnabled(false);
                editPswNewConfirm.setEnabled(false);
            }
        });

        btnChangePregSeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changingPregSeg = true;
                btnChangePregSeg.setEnabled(false);
                linearChangePregSeg.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changingPassword = false;
                btnChangePsw.setEnabled(true);
                editPswActual.setText("");
                editPswNew.setText("");
                editPswNewConfirm.setText("");
                linearChangePsw.setVisibility(View.GONE);
            }
        });

        editPswActual.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals("")){
                    if(editable.toString().length()==8){
                        editPswNew.setEnabled(true);
                    }else{
                        editPswNew.setEnabled(false);
                    }
                }else{
                    editPswNew.setEnabled(false);
                }
            }
        });

        editPswNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals("")){
                    if(editable.toString().length()==8){
                        editPswNewConfirm.setEnabled(true);
                    }else{
                        editPswNewConfirm.setEnabled(false);
                    }
                }else{
                    editPswNewConfirm.setEnabled(false);
                }
            }
        });

        btnUpdatePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contraseñaAct = editPswActual.getText().toString();
                String mail = editEmail.getText().toString();

                actualizarContraseña(mail,contraseñaAct);
                /*if(resultContraseñaActual){

                    String pswNuevo = editPswNew.getText().toString();
                    String pswNuevoConf = editPswNewConfirm.getText().toString();

                    if(pswNuevoConf.equals(pswNuevo)){
                        mUser.updatePassword(pswNuevoConf).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Contraseña Cambiada con Éxito.", Toast.LENGTH_SHORT).show();     
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }else{
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
                        dialogo.setIcon(getResources().getDrawable(R.drawable.ic_alert))
                                .setMessage("La Nueva contraseña y la Confirmación No corresponden, verifique sean las mismas.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).setCancelable(false)
                                .create();
                        dialogo.show();
                    }
                    
                }else {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
                    dialogo.setIcon(getResources().getDrawable(R.drawable.ic_red_error))
                            .setMessage("La contraseña Actual No corresponde al usuario Autenticado." +
                                    "Ingrese la correcta.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editPswActual.setText("");
                                    editPswNew.setText("");
                                    editPswNewConfirm.setText("");
                                    dialogInterface.dismiss();
                                }
                            }).setCancelable(false)
                            .create();
                    dialogo.show();
                }*/
            }
        });

        final ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions_es,R.layout.smallertext_spinner);
        //final String[] pregs = getResources().getStringArray(R.array.security_questions_es);

        spinnerPregSeg.setAdapter(spAdapter);
        ArrayAdapter arAdapter = (ArrayAdapter) spinnerPregSeg.getAdapter();
        pregPos = arAdapter.getPosition(preguntaDeSeg);
        spinnerPregSeg.setSelection(pregPos);
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
                //int posicion = 0;
                /*for(int i = 0;i<pregs.length;i++ ){
                    if(pregs.equals(preguntaDeSeg)){
                        adapterView.setSelection(i);
                    }
                }*/
                //adapterView.setSelection(4);
                //pregSegSpinner.setSelection(adapterView.getFirstVisiblePosition());
                editResSeg.setVisibility(View.GONE);
            }
        };
        spinnerPregSeg.setOnItemSelectedListener(spinnerListener);

        btnCancelPreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changingPregSeg = false;
                btnChangePregSeg.setEnabled(true);
                spinnerPregSeg.setSelection(pregPos);
                editResSeg.setText("");
                linearChangePregSeg.setVisibility(View.GONE);
            }
        });

        btnSavePreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changingPregSeg = false;
                btnChangePregSeg.setEnabled(true);
                linearChangePregSeg.setVisibility(View.GONE);
            }
        });

    }


    private void actualizarContraseña(String email, final String contraseñaActual){

        try{

            mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION)
                    .document(email+"-"+contraseñaActual)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    preguntaDeSeg = documentSnapshot.get("pregSeguridad").toString();
                    resPregunta = documentSnapshot.get("resSeguridad").toString();
                    contraseñaFromDB = documentSnapshot.getString("password");
                    if(documentSnapshot.get("password").toString().equals(contraseñaActual)){
                        //resultContraseñaActual = true;
                        //if(resultContraseñaActual){

                            String pswNuevo = editPswNew.getText().toString();
                            String pswNuevoConf = editPswNewConfirm.getText().toString();

                            if(pswNuevoConf.equals(pswNuevo)){
                                mUser.updatePassword(pswNuevoConf).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        changingPassword = false;
                                        Toast.makeText(context, "Contraseña Actualizada con Éxito.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error al actualizar la Contraseña.", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                });
                            }else{
                                AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
                                dialogo.setIcon(getResources().getDrawable(R.drawable.ic_alert))
                                        .setMessage("La Nueva contraseña y la Confirmación No corresponden, verifique sean las mismas.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).setCancelable(false)
                                        .create();
                                dialogo.show();
                            }
                        //}else {

                        //}
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    //resultContraseñaActual = false;
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
                    dialogo.setIcon(getResources().getDrawable(R.drawable.ic_red_error))
                            .setMessage("La contraseña Actual No corresponde al usuario Autenticado." +
                                    "Ingrese la correcta.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editPswActual.setText("");
                                    editPswNew.setText("");
                                    editPswNewConfirm.setText("");
                                    dialogInterface.dismiss();
                                }
                            }).setCancelable(false)
                            .create();
                    dialogo.show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        //return resultContraseñaActual;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_accconfig_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(allInfoSaved){
                    String sendPregSeg = "";
                    if(fromTipoInvita){
                        if(!pregSeg.equals("")||pregSeg.length()!=0){
                            sendPregSeg = pregSeg;
                        }else{
                            sendPregSeg = preguntaDeSeg;
                        }
                        startActivity(new Intent(context,TipoInvitaActivity.class)
                                .putExtra("nombre",username)
                                .putExtra("email",email)
                                .putExtra("psw",pswFromIntent)
                                .putExtra("pregSeguridad",sendPregSeg)
                                .putExtra("resSeguridad",resPregunta)
                                //.putExtra("img_str",avatar_img_str)
                        );
                    }else{
                        if(!pregSeg.equals("")||pregSeg.length()!=0){
                            sendPregSeg = pregSeg;
                        }else{
                            sendPregSeg = preguntaDeSeg;
                        }
                        startActivity(new Intent(context,InicioActivity.class)
                                .putExtra("nombre",username)
                                .putExtra("email",email)
                                .putExtra("psw",pswFromIntent)
                                .putExtra("pregSeguridad",sendPregSeg)
                                .putExtra("resSeguridad",resPregunta)
                                //.putExtra("img_str",avatar_img_str)
                        );
                    }
                }else{
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setMessage("Se requiere guardar los cambios antes de avandonar la Configuración de la cuenta.")
                            .setIcon(R.drawable.ic_alert)
                            .setTitle("Mensaje")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
                break;
            case R.id.btnSaveAccConfig:
                nom = editNombre.getText().toString();
                mail = editEmail.getText().toString();
                pass = editPswNewConfirm.getText().toString();
                respuestaSeg = editResSeg.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if(validarInfo(nom,mail,pass,respuestaSeg)){
                    uploadImageToFireStorage(decodedString);
                    //updateAccInfo(nom,mail,pass,respuestaSeg);
                }

        }
        return super.onOptionsItemSelected(item);
    }
    byte[] decodedString;
    private void getUsuario2(String email, String psw){
        //try{

            Bitmap userBitmap = null;
            account = new Account();
            dRef = mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        //Account acc = new Account();
                        avatar_img_str = "";
                        DocumentSnapshot snapshot = task.getResult();
                        account.setNombre(snapshot.get("nombre").toString());
                        account.setEmail(snapshot.get("email").toString());
                        account.setImg_string(snapshot.get("img_string").toString());
                        account.setImg_storage_path(snapshot.get("img_storage_path").toString());
                        avatar_storage_path = account.getImg_storage_path();
                        avatar_img_str = account.getImg_string();
                        //textUserName = (TextView)findViewById(R.id.textUserName);
                        imgProfile = (CircleImageView)findViewById(R.id.imgProfile);
                        editNombre = findViewById(R.id.editNombre);
                        editEmail =  findViewById(R.id.editEmail);
                        editNombre.setText(account.getNombre());
                        editEmail.setText(account.getEmail());
                        if(!avatar_storage_path.equals("")){

                            //decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                            downloadProfilePicFromFireStorage();
                            //bmToSave = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //Glide.with(context).load(bmToSave).into(imgProfile);
                            Glide.with(context).load(avatar_storage_path).into(imgProfile);
                            conFotoPerfil = true;
                        }else{
                            imgProfile.setBackground(getResources().getDrawable(R.drawable.ic_default_user_accentdark));
                            conFotoPerfil = false;
                        }
                        //mTools.setStringPreference("user",account.getNombre());
                        //mTools.setStringPreference("email",account.getEmail());
                        //mTools.setStringPreference("img_str",account.getImg_string());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        //}catch (Exception e){
            //e.printStackTrace();
        //}
    }

    private boolean validarInfo(String name,String email, String psw,String respuestaS){

        boolean res = true;

        if(name.equals("")||name.length()<5){
            editNombre.setError("Nombre requerido.");
            //res = false;
            progressBar.setVisibility(View.GONE);
            return false;
        }
        if (email.equals("")) {
            editEmail.setError("Email requerido.");
            //res = false;
            progressBar.setVisibility(View.GONE);
            return false;
        }
        //if(psw.equals("")||psw.length()==0){
            //if(pswFromIntent.equals()){

            //}
            //editPsw.setError("Password requerido.");
            //res = false;
            //progressBar.setVisibility(View.GONE);
            //return false;
        //}
        Matcher pEmail =  Patterns.EMAIL_ADDRESS.matcher(email);
        if(!pEmail.matches()){
            editEmail.setError("No es email valido.");
            //res = false;
            progressBar.setVisibility(View.GONE);
            return false;
        }
        //if(psw.length()>8){
            //editPsw.setError("Contraseña debe ser No mayor de 8 caracteres.");
            //res = false;
            //return false;
        //}

        if(spinnerPregSeg.getSelectedItem().toString().equals("N/A")){
            ((TextView)spinnerPregSeg.getSelectedView()).setError("Pregunta de Seguridad requerida");
            //res = false;
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if(changingPregSeg){
            if(editResSeg.getText().toString().equals("")||editResSeg.getText().toString().length()==0){
                editResSeg.setError("Respuesta de Seguridad requerida");
                //res = false;
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(conFotoPerfil){
            if(bmToSave!=null){
                bmToSave.compress(Bitmap.CompressFormat.JPEG,70,stream);
                decodedString = stream.toByteArray();
            }
            //data_image = stream.toByteArray();
            //avatar_img_str = Base64.encodeToString(data_image, Base64.DEFAULT);
        }else{
            //Usuario sin foto de perfil.
            decodedString = null;
            avatar_img_str = "";
        }

        return res;
    }

    private void updateAccInfo(final String mNombre, final String mEmail, final String mPassword, String rS, Uri uri){

            Map<String, Object> updateAcc = new HashMap<>();
            //updateAcc.put("img_string",avatar_img_str);
            if(uri!=null){
                updateAcc.put("img_storage_path",uri.toString());
            }else{
                updateAcc.put("img_storage_path","");
            }
            updateAcc.put("nombre",mNombre);
            updateAcc.put("email",mEmail);
            if(!mPassword.equals("")||mPassword.length()!=0){
                updateAcc.put("password",mPassword);
            }else{
                updateAcc.put("password",pswFromIntent);
            }
            if(!pregSeg.equals("")||pregSeg.length()!=0){
                updateAcc.put("pregSeguridad",pregSeg);
            }else{
                updateAcc.put("pregSeguridad",preguntaDeSeg);
            }
            if(!rS.equals("")||rS.length()!=0){
                updateAcc.put("resSeguridad",rS);
            }else{
                updateAcc.put("resSeguridad",resPregunta);
            }

            mFireStoreDB.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION)
                    .document(mEmail+"-"+pswFromIntent)
                    .update(updateAcc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            allInfoSaved = true;
                            progressBar.setVisibility(View.GONE);
                            mUser.updateEmail(mEmail);

                            mUser.updateProfile(new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mNombre).build());

                            Toast.makeText(context, "Información de la cuenta actualizada con Éxito."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("Error.")
                                    .setIcon(R.drawable.ic_red_error)
                                    .setMessage(e.getMessage())
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            progressBar.setVisibility(View.GONE);
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        }
                    });

    }

    private void downloadProfilePicFromFireStorage(){
        //mStorageRef = mFirebaseStorage.getReference();
        //StorageReference carpeta = mStorageRef.child("singulariaUserProfilePic/");
        //StorageReference imagen  = carpeta.child(mUser.getUid()+".jpg");
        final long TEN_MB = (1024 * 1024)*10;
        StorageReference imagen = mFirebaseStorage.getReferenceFromUrl(avatar_storage_path);
        imagen.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                decodedString = bytes;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void uploadImageToFireStorage(byte[] byteArrayImage){


        //String fecha = tools.getFecha();
        Uri uri = null;
        if(byteArrayImage!=null){
            InputStream byteArrayIS = new ByteArrayInputStream(byteArrayImage);
            mStorageRef = mFirebaseStorage.getReference();
            StorageReference carpeta = mStorageRef.child("singulariaUserProfilePic/");
            //StorageReference imagen = carpeta.child(Calendar.getInstance().getTimeInMillis()+"|"+fecha+".jpg");
            StorageReference imagen  = carpeta.child(mUser.getUid()+".jpg");
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
                                    //saveIntoDB(username,email,psw,uri);
                                    updateAccInfo(nom,mail,pass,respuestaSeg, uri);
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
            updateAccInfo(nom,mail,pass,respuestaSeg, uri);
        }

    }

    private void takePictureIntent() {

        AlertDialog.Builder dialogProfPic = new AlertDialog.Builder(this);
        dialogProfPic.setTitle("Picture");
        dialogProfPic.setPositiveButton("De Galería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/");
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
                Glide.with(context).load(bm).into(imgProfile);
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
                Glide.with(context).load(bm).into(imgProfile);
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
            e.printStackTrace();
            Log.e("", "-- Error in setting image");
        }catch (OutOfMemoryError oom){
            oom.printStackTrace();
            Log.e("", "-- OOM Error in setting image");
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
    }
}
