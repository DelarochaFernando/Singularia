package com.delarocha.singularia.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.delarocha.singularia.R;
import com.delarocha.singularia.activities.ImageGalleryActivity;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.media.WrapMotionEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImagePreview extends AppCompatActivity implements View.OnTouchListener {

    private FrameLayout flContainerBitmap;
    private FloatingActionButton btnBack,fabDeleteImg;
    private ImageView imagePreviewFullSize;
    private String email,psw,username,img_str,UID, imgStoragePath;
    private Bitmap bitmapPhoto, bitmapdecoded, bmIMG;
    private byte[] byteArrImg;
    private static final String TAG = "ImagePreview";
    private Context context = this;
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
    private Tools tools;
    private boolean fromCamera = true;
    private FirebaseStorage mStorageRef;
    private FirebaseFirestore mFirestore;
    private DocumentReference mDocRef;
    private boolean fromInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        psw = extras.getString("psw");
        username = extras.getString("nombre");
        img_str = extras.getString("img_str");
        byteArrImg = extras.getByteArray("byteArrImg");
        fromCamera = extras.getBoolean("fromCamera");
        UID = extras.getString("UID");
        imgStoragePath = extras.getString("imgStoragePath");
        fromInicio = extras.getBoolean("fromInicio");
        //bmIMG = extras.getParcelable("BM");

        tools = new Tools(context);
        mStorageRef = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        flContainerBitmap = findViewById(R.id.flContainerBitmap);
        imagePreviewFullSize = findViewById(R.id.imagePreviewFullSize);
        btnBack = findViewById(R.id.btnBack);
        fabDeleteImg = findViewById(R.id.fabDeleteImg);

        imagePreviewFullSize.setOnTouchListener(this);
        matrix.setTranslate(1f, 1f);
        imagePreviewFullSize.setImageMatrix(matrix);

        decodePhoto();

        maxZoom    =   3.f;
        minZoom    =   0.5f;
        //height = imagePreviewFullSize.getDrawable().getIntrinsicHeight();
        //width = imagePreviewFullSize.getDrawable().getIntrinsicWidth();
        viewRect = new RectF(0, 0, imagePreviewFullSize.getWidth(), imagePreviewFullSize.getHeight());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        fabDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setTitle("¿Desea eliminar la imagen?");
                deleteDialog.setMessage("¿Desea eliminar la imagen?");
                deleteDialog.setMessage("La imagen No se podrá recuperar Nuevamente.");
                deleteDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteImageFromStorage();
                    }
                });
                deleteDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      dialogInterface.dismiss();
                    }
                });
                deleteDialog.setCancelable(false);
                deleteDialog.create();
                deleteDialog.show();
            }
        });
    }

    private void back(){

        if(fromCamera){
            startActivity(new Intent(ImagePreview.this,SingulariaCam.class)
                    .putExtra("email",email)
                    .putExtra("psw",psw)
                    .putExtra("nombre",username)
                    .putExtra("img_str",img_str)
                    .putExtra("fromInicio",fromInicio)
            );
        }else{
            startActivity(new Intent(ImagePreview.this, ImageGalleryActivity.class)
                    .putExtra("email",email)
                    .putExtra("psw",psw)
                    .putExtra("nombre",username)
                    .putExtra("img_str",img_str)
                    .putExtra("fromInicio",fromInicio)
            );
        }
    }
    private void decodePhoto(){
        try{
            //byte[] data = Base64.decode(img_str,0);
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = true;
            //options.inPreferredConfig= Bitmap.Config.RGB_565;
            //BitmapFactory.decodeByteArray(data, 0, data.length, options);
            //options.inJustDecodeBounds = false;
            /*if(fromCamera){
                if(byteArrImg!=null){

                }
            }*/

            //bitmapPhoto = BitmapFactory.decodeByteArray(byteArrImg, 0, byteArrImg.length, options);
            //bitmapdecoded = tools.getBitmapOrientationAdjusted(bitmapPhoto);

            //Glide.with(context).load(data)
            //Glide.with(context).load(bitmapdecoded)
            //Glide.with(context).load(bmIMG)
            Glide.with(context).load(imgStoragePath)
             .apply(new RequestOptions().centerCrop())
             //.transition(DrawableTransitionOptions.withCrossFade())
             .into(imagePreviewFullSize);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteImageFromStorage(){

        StorageReference imagenRef = mStorageRef.getReferenceFromUrl(imgStoragePath);
        imagenRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(context, "Imagen eliminada correctamente", Toast.LENGTH_SHORT).show();
                Log.i("IMG_DELETED_FireStorage","Imagen eliminada de FireStorage Correctamente");
                deleteRecordFromFireStore(imgStoragePath);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error al eliminar la Imagen", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void deleteRecordFromFireStore(final String storagePath){

        final DocumentSnapshot[] document = new DocumentSnapshot[1];
        mFirestore.collection(Tools.FIRESTORE_GALLERY_COLLECTION)
                .whereEqualTo("imgStoragePath",storagePath).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot qdocSnap: task.getResult()){

                                qdocSnap.getReference().delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful()){
                                                  Log.i("IMG_DELETED_FireStore","Imagen eliminada correctamente");
                                                  Toast.makeText(ImagePreview.this, "Imagen eliminada Correctamente.", Toast.LENGTH_SHORT).show();
                                                  back();
                                              }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                            }
                                        });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
                /*.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(int i = 0;i<task.getResult().getDocuments().size();i++){
                                document[0] = task.getResult().getDocuments().get(i);
                                if(document[0].getId().contains(UID)){
                                    String imgPath = document[0].getString("imgStoragePath");
                                    if(imgPath.equals(storagePath)){
                                        document[0].getDocumentReference()
                                    }
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });*/

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
                    //Comentarizado para deshabilitar la funcionalidad de mover la foto.

                    //matrix.set(savedMatrix);
                    //matrix.postTranslate((event1.getX()) - start.x,(event1.getY()) - start.y);
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
}
