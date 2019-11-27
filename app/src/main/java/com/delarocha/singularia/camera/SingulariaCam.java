package com.delarocha.singularia.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
//import org.kobjects.base64.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.delarocha.singularia.R;
import com.delarocha.singularia.activities.InicioActivity;
import com.delarocha.singularia.activities.TipoInvitaActivity;
import com.delarocha.singularia.auxclasses.ImageSavedPath;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.media.WrapMotionEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SingulariaCam extends AppCompatActivity implements View.OnTouchListener {

    private FrameLayout cameraFrame, framePImage;
    private String email, username, psw;
    private FloatingActionButton fabTakePicture, btnBackCam;
    //private MaterialButton btnBackCam;
    private Context context = this;
    private PreviewCam mPreviewCam;
    private ImageView imageViewFotoTomada, imgPhotoPreview;
    private LinearLayout linearPreview;
    private ProgressBar progressBCam;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean sp_lock_device_rotation = false;
    private SensorManager mSensor;
    private static final String TAG = "SingulariaCam";

    private FirebaseFirestore mFirestoreDB;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private Date currentDate;
    private Tools tools;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    float[] matrixValues = new float[9];
    private RectF viewRect;
    private float width, height,maxZoom,minZoom;
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 4f;
    Bitmap bitmapDecoded;
    Bitmap bitmapProcessedPhoto;
    String data_str = "";
    private String pregSeg, resSeguridad;
    byte[] byteArrImgIntent;
    private boolean fromInicio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singularia_cam);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        psw = extras.getString("psw");
        username = extras.getString("nombre");
        pregSeg = extras.getString("pregSeguridad");
        resSeguridad = extras.getString("resSeguridad");
        fromInicio = extras.getBoolean("fromInicio");
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        mAuth = FirebaseAuth.getInstance();
        mFirestoreDB = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        tools = new Tools(context);


        cameraFrame = findViewById(R.id.cameraFrame);
        framePImage = findViewById(R.id.framePImage);
        imageViewFotoTomada = findViewById(R.id.imageViewFotoTomada);
        linearPreview = findViewById(R.id.linearPreview);
        imgPhotoPreview = findViewById(R.id.imgPhotoPreview);
        fabTakePicture = findViewById(R.id.fabTakePicture);
        btnBackCam = findViewById(R.id.btnBackCam);
        progressBCam = findViewById(R.id.progressBCam);



        btnBackCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromInicio){
                    startActivity(new Intent(context, InicioActivity.class)
                            .putExtra("email", email)
                            .putExtra("psw", psw)
                            .putExtra("nombre", username)
                            .putExtra("pregSeguridad",pregSeg)
                            .putExtra("resSeguridad",resSeguridad)
                    );
                }else{
                    startActivity(new Intent(context, TipoInvitaActivity.class)
                            .putExtra("email", email)
                            .putExtra("psw", psw)
                            .putExtra("nombre", username)
                            .putExtra("pregSeguridad",pregSeg)
                            .putExtra("resSeguridad",resSeguridad)
                    );
                }

            }
        });

        fabTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmapDecoded!=null){
                    bitmapDecoded.recycle();
                }
                if(bitmapProcessedPhoto!=null){
                    bitmapProcessedPhoto.recycle();
                }
                captureImage();
            }
        });

        mPreviewCam = new PreviewCam(this);//responsable for show the Camera environment
        cameraFrame.addView(mPreviewCam);

        //Evento touch para la fotografía
        //imageViewFotoTomada.setOnTouchListener(SingulariaCam.this);
        //matrix.setTranslate(1f, 1f);
        //imageViewFotoTomada.setImageBitmap(bitmapProcessedPhoto);
       // imageViewFotoTomada.setImageMatrix(matrix);

        //maxZoom    =   3.f;
        //minZoom    =   0.5f;
        //height = imageViewFotoTomada.getDrawable().getIntrinsicHeight();
        //width = imageViewFotoTomada.getDrawable().getIntrinsicWidth();
        //viewRect = new RectF(0, 0, imageViewFotoTomada.getWidth(), imageViewFotoTomada.getHeight());

        imgPhotoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //try{
                    startActivity(new Intent(SingulariaCam.this,ImagePreview.class)
                            .putExtra("email",email)
                            .putExtra("psw",psw)
                            .putExtra("nombre",username)
                            .putExtra("imgStoragePath",imgTakenStorage)
                            .putExtra("UID",mAuth.getCurrentUser().getUid())
                            .putExtra("fromCamera",true)
                    );
                //}catch (Exception e){
                    //e.printStackTrace();
                //}
            }
        });
        //clearStorageCameraParams();

        /*mSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor.registerListener((SensorEventListener) SingulariaCam.this,
                mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);*/
    }

    public void clearLockRotation()
    {
        try
        {
            sp_lock_device_rotation = false;
            editor.putBoolean("sp_lock_device_rotation", sp_lock_device_rotation);
            editor.commit();
        }
        catch(Exception ex){ ex.printStackTrace();}
    }

    public void captureImage(){

        try {
            mPreviewCam.camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {

                        mPreviewCam.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                    }
                }
            });
        } catch (Throwable t) {
            //toastWDCamera("Se ha producido un error. Intente de nuevo.", "3");
        }
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {

        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] bytesArr, Camera camera) {

            procesarImagen(bytesArr);

        }
    };

    public void procesarImagen(byte[] byteArray){

        AsyncPhotoProcess mPhotoProcess = new AsyncPhotoProcess(byteArray);
        mPhotoProcess.execute();
    }


    private class AsyncPhotoProcess extends AsyncTask<byte[],Void,Boolean>{
        byte[] data = null;

        public AsyncPhotoProcess(byte[] dataArray){
            this.data = dataArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBCam.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(byte[]... bytes) {

            //try{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig= Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, options);
                options.inJustDecodeBounds = false;
                bitmapDecoded = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                Bitmap bitmap = tools.getBitmapOrientationAdjusted(bitmapDecoded);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream1);

                bitmapDecoded.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] data_image = stream1.toByteArray();
                byteArrImgIntent = stream.toByteArray();

                data_str = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                //Save the data_str into FireBaseDB
                //saveIntoDB(username,email,psw);

                //Save image into FirebaseStorage
                uploadImageToFireStorage(data_image);

            //}catch(Exception e){
               // e.printStackTrace();
            //}finally{
                //return true;
            //}
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBCam.setVisibility(View.GONE);
            linearPreview.setVisibility(View.VISIBLE);
            bitmapProcessedPhoto = tools.getBitmapOrientationAdjusted(bitmapDecoded);
            //float wdpfloat = 120f;
            //float hdpfloat = 50f;
            //int w = (int) Tools.convertDpToPixel(context,wdpfloat);
            //int h = (int) Tools.convertDpToPixel(context,hdpfloat);
            Glide.with(context).load(bitmapProcessedPhoto)
            //Glide.with(context).load(bitmapDecoded)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imgPhotoPreview);
            //imgPhotoPreview.setImageBitmap(bitmapProcessedPhoto);
            /*startActivity(new Intent(SingulariaCam.this,PreviewCam.class)
            .putExtra("img_str",data_str)
            );*/
        }
    }

    String imgTakenStorage = "";
    private void uploadImageToFireStorage(byte[] byteArrayImage){


            String fecha = tools.getFecha();
            InputStream byteArrayIS = new ByteArrayInputStream(byteArrayImage);
            mStorageRef = mFirebaseStorage.getReference();
            StorageReference carpeta = mStorageRef.child("singulariaImagesGallery/"+mUser.getUid());
            StorageReference imagen = carpeta.child(Calendar.getInstance().getTimeInMillis()+"|"+fecha+".jpg");
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
                            saveIntoDB(username,email,psw,uri);
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
    }
    private void saveIntoDB(String name , String emailAddress, String password, Uri imgUri) {

        //try{

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            final String[] URL_Imagen = new String[1];
            //Date mDate = sdf.parse(fec_alta);
            //long dateInMiliseconds = mDate.getTime();
            //String dateMiliText = String.valueOf(dateInMiliseconds);

            Calendar calendar = Calendar.getInstance();
            currentDate = calendar.getTime();
            long dateInMiliseconds = calendar.getTimeInMillis();
            String imei = Tools.getIMEI(context);
            String dayString = "";
            String monthString = "";
            String minuteString = "";
            String secondString = "";
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day   = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            if(month<10){
                monthString = "0"+month;
            }else{
                monthString = String.valueOf(month);
            }
            if(day<10){
                dayString = "0"+day;
            }else{
                dayString = String.valueOf(day);
            }
            if(minute<10){
                minuteString = "0"+minute;
            }else{
                minuteString = String.valueOf(minute);
            }
            if(second<10){
                secondString = "0"+second;
            }else{
                secondString = String.valueOf(second);
            }

        final String fecha = String.valueOf(year+"-"+monthString+"-"+dayString+"-"+hour+":"+minuteString+":"+secondString);
        //SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //final Date fecha = simpleDate.parse(currentDate.toString());
        final Map<String, Object> imageTaken = new HashMap<>();
        //String nombre = name;
        String uid = mUser.getUid();
        //ImageSavedPath savedPath = new ImageSavedPath(metadata.getPath());
        //imageTaken.put("imgStoragePath",URL_Imagen[0]);
        imageTaken.put("imgStoragePath",imgUri.toString());
        imageTaken.put("uid",uid);
        //imageTaken.put("imgBitmap",bitmapDecoded);
        imageTaken.put("nombre",name);
        imageTaken.put("email",emailAddress);
        imageTaken.put("password",password);
        imageTaken.put("device_imei", imei);
        imageTaken.put("fecha",currentDate.toString());
        final String label = uid+"|"+fecha;
        imgTakenStorage = imgUri.toString();

                    mFirestoreDB.collection(Tools.FIRESTORE_GALLERY_COLLECTION)
                            .document(label)
                            .set(imageTaken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void avoid) {
                                    Log.i(TAG,"foto guardada exitosamente..."+label);
                                    //createdSuccessfully = true;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG,"Error al guardar la fotografía..."+label);
                            e.printStackTrace();
                            //createdSuccessfully = false;
                        }
                    });

    }

    public boolean onTouch(View v, MotionEvent rawEvent)
    {
        WrapMotionEvent event1 = WrapMotionEvent.wrap(rawEvent);
        ImageView imagePreviewCamera = (ImageView) v;
        dumpEvent(event1);
        switch (event1.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event1.getX(), event1.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event1);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event1);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate((event1.getX()) - start.x,(event1.getY()) - start.y);
                }
                else if (mode == ZOOM)
                {
                    float newDist = spacing(event1);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f)
                    {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        float[] f = new float[9];
                        matrix.getValues(f);
                        float currentScale    =   f[Matrix.MSCALE_X];
                        if(scale * currentScale > maxZoom)
                            scale = maxZoom / currentScale;
                        else if(scale * currentScale < minZoom)
                            scale = minZoom / currentScale;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        imagePreviewCamera.setImageMatrix(matrix);
        return true; // indicate event was handled
    }

    private void dumpEvent(WrapMotionEvent event) {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d(TAG, sb.toString());
    }


    private float spacing(WrapMotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, WrapMotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
    }
}
