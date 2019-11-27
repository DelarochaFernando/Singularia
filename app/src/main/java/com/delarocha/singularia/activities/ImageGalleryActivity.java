package com.delarocha.singularia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.camera.ImagePreview;
import com.delarocha.singularia.database.SingulariaDBHelper;
import com.delarocha.singularia.gallery.ImageFromFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerGallery;
    private String mEmail,mPsw;
    private DocumentReference dRef;
    private CollectionReference collRef;
    private FirebaseFirestore mFirestoreDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String UID = "";
    private String pregSeg, resSeguridad, mNombre;
    private ImageFromFireBase mImageFromFireB;
    private List<ImageFromFireBase> imageList, imageListOffline;
    private Context ctx = this;
    private ImagesFromDBAdapter imagesFromDBAdapter;
    private Tools tools;
    private LinearLayout linearDownloadingImages;
    private TextView txtDownImages, txtNoImages;
    private ImageView imgNoImages;
    private ContentLoadingProgressBar loadingProgressBar;
    private SingulariaDBHelper singulariaDB;
    private StorageReference storageRef, pathRef;
    private int densityDpi;
    private boolean fromInicio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        getSupportActionBar().setTitle(R.string.ImageGalleryActivityTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                densityDpi = DisplayMetrics.DENSITY_LOW;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                //Lanix IliumPad L8 7"
                densityDpi = DisplayMetrics.DENSITY_MEDIUM;
                //setContentView(R.layout.activity_image_gallery);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                densityDpi = DisplayMetrics.DENSITY_HIGH;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                //Sony XPeria Z3 Compact 8"
                densityDpi = DisplayMetrics.DENSITY_XHIGH;
                //setContentView(R.layout.activity_image_gallery);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                densityDpi = DisplayMetrics.DENSITY_XXHIGH;
                break;
            case DisplayMetrics.DENSITY_TV:
                //Samsung Galaxy Tab A 2017 8"
                //setContentView(R.layout.wd_camera_preview_tvdpi);
                densityDpi = DisplayMetrics.DENSITY_TV;
                break;
            default:
                //setContentView(R.layout.activity_image_gallery);
        }

        Bundle extras = getIntent().getExtras();
        mEmail = extras.getString("email");
        mPsw = extras.getString("psw");
        mNombre = extras.getString("nombre");
        pregSeg = extras.getString("resSeguridad");
        resSeguridad = extras.getString("pregSeguirdad");
        fromInicio = extras.getBoolean("fromInicio");
        tools = new Tools(ctx);

        mFirestoreDB = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        pathRef = storageRef.child("gs://singularia-fase1-2018.appspot.com/DSC_0052[1].JPG");
        singulariaDB = new SingulariaDBHelper(ctx);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        imageList = new ArrayList();
        imageListOffline = new ArrayList<>();

        UID = mUser.getUid();
        linearDownloadingImages = findViewById(R.id.linearDownloadingImages);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        imgNoImages = findViewById(R.id.imgNoImages);
        txtDownImages = findViewById(R.id.txtDownImages);
        txtNoImages = findViewById(R.id.txtNoImages);
        recyclerGallery = findViewById(R.id.recyclerGallery);
        //recyclerGallery.setVisibility(View.VISIBLE);

        //imageListOffline = singulariDB.getAllGalleryImages(UID);
        //if(imageListOffline==null){
            getImagesFromFireBase(mEmail, mPsw);
        //}

        fetchImages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                if(fromInicio){
                    startActivity(new Intent(ctx,InicioActivity.class)
                            .putExtra("email", mEmail)
                            .putExtra("psw", mPsw)
                            .putExtra("nombre",mNombre)
                            .putExtra("pregSeguridad",pregSeg)
                            .putExtra("resSeguridad",resSeguridad)
                            .putExtra("fromInicio",fromInicio)
                    );
                }else{
                    startActivity(new Intent(ctx,TipoInvitaActivity.class)
                            .putExtra("email", mEmail)
                            .putExtra("psw", mPsw)
                            .putExtra("nombre",mNombre)
                            .putExtra("pregSeguridad",pregSeg)
                            .putExtra("resSeguridad",resSeguridad)
                            .putExtra("fromInicio",fromInicio)
                    );
                }
             break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void fetchImages(){
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference carpeta = ref.child("singulariaImagesGallery/");

        List<FileDownloadTask> tasks = ref.getActiveDownloadTasks();
        if(tasks.size()>0){
            FileDownloadTask task = tasks.get(0);
            task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                }
            });
        }

    }

    private void getImagesFromFireBase(String email, String password){

        try{
            imageListOffline = new ArrayList<>();
            mFirestoreDB.collection(Tools.FIRESTORE_GALLERY_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                linearDownloadingImages.setVisibility(View.GONE);
                                txtNoImages.setVisibility(View.GONE);
                                imgNoImages.setVisibility(View.GONE);
                                //recyclerGallery = findViewById(R.id.recyclerGallery);
                                recyclerGallery.setVisibility(View.VISIBLE);

                                //List<QueryDocumentSnapshot> docsList = (ArrayList<QueryDocumentSnapshot>)task.getResult();
                                //for(QueryDocumentSnapshot document: task.getResult()){
                                for(int i = 0; i<task.getResult().getDocuments().size();i++){
                                    DocumentSnapshot document = task.getResult().getDocuments().get(i);
                                    if(document.getId().contains(UID)){
                                        mImageFromFireB = new ImageFromFireBase();
                                        mImageFromFireB.setUid(document.getString("uid"));
                                        mImageFromFireB.setDevice_imei(document.getString("device_imei"));
                                        mImageFromFireB.setImgStoragePath(document.getString("imgStoragePath"));
                                        mImageFromFireB.setEmail(document.getString("email"));
                                        mImageFromFireB.setNombre(document.getString("nombre"));
                                        mImageFromFireB.setFecha(document.getString("fecha"));
                                        mImageFromFireB.setPassword(document.getString("password"));
                                        imageList.add(mImageFromFireB);
                                        singulariaDB.insertGalleryImage(mImageFromFireB);
                                    }
                                }
                                //imageListOffline = singulariDB.getAllGalleryImages(mImageFromFireB.getUid());
                                if(imageList.size()==0){
                                    linearDownloadingImages.setVisibility(View.VISIBLE);
                                    loadingProgressBar.setVisibility(View.GONE);
                                    recyclerGallery.setVisibility(View.GONE);
                                    txtDownImages.setVisibility(View.GONE);
                                    txtNoImages.setVisibility(View.VISIBLE);
                                    imgNoImages.setVisibility(View.VISIBLE);
                                }else{
                                    imagesFromDBAdapter = new ImagesFromDBAdapter(imageList);
                                    GridLayoutManager mGridLayoutManager;
                                    double diagInch = Tools.getDiagonalInch(ctx);
                                    if(diagInch<9 && diagInch>=6.5) {
                                        // small tab (7 inch tab)
                                        mGridLayoutManager = new GridLayoutManager(ctx,3);
                                    } else if(diagInch>9) {
                                        // big tab (10 inch tab)
                                        mGridLayoutManager = new GridLayoutManager(ctx,3);
                                    } else {
                                        //phones s2,s3 s4 etc devices
                                        mGridLayoutManager = new GridLayoutManager(ctx,3);
                                    }
                                    //GridLayoutManager mGridLayoutManager = new GridLayoutManager(ctx,2);
                                    recyclerGallery.setAdapter(imagesFromDBAdapter);
                                    recyclerGallery.setLayoutManager(mGridLayoutManager);
                                    recyclerGallery.setHasFixedSize(true);
                                }
                                //InitialSetUp();
                            }else{
                                linearDownloadingImages.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //imageListOffline = new ArrayList<>();
        //imageListOffline = singulariDB.getAllGalleryImages(UID);
        //InitialSetUp();
        /*if(imageListOffline.size()!=0){
            if(imageListOffline.size() == imageList.size()){
                InitialSetUp();
            }else{
                getImagesFromFireBase(mEmail,mPsw);
            }
        }*/
        //getImagesFromFireBase(mEmail, mPsw);
    }

    public void InitialSetUp(){

        imagesFromDBAdapter = new ImagesFromDBAdapter(imageListOffline);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this,2);
        //GridLayoutManager mGridLayoutManager3 = new GridLayoutManager(this,3);
        recyclerGallery.setAdapter(imagesFromDBAdapter);
        recyclerGallery.setLayoutManager(mGridLayoutManager);
        recyclerGallery.setHasFixedSize(true);
    }

    private class ImagesFromDBAdapter extends RecyclerView.Adapter<ImagesFromDBAdapter.ImageVH>{

        List<ImageFromFireBase> imageneslista;
        List<Bitmap> mBitmapList;
        List<byte[]> mByteArrList;
        public ImagesFromDBAdapter(List<ImageFromFireBase> list){
            this.imageneslista = list;
            //setUpBitmapRodatedLIst(imageneslista);
        }

        protected void setUpBitmapRodatedLIst(List<ImageFromFireBase> imagesList){

            try{
                int i = 0;
                int imagesCount = imagesList.size();
                mBitmapList = new ArrayList<>();
                mByteArrList = new ArrayList<>();
                for(ImageFromFireBase imagen : imagesList){

//                    byte[] byteArr =Base64.decode(imagen.getImg_string(),Base64.DEFAULT);
//                    mByteArrList.add(byteArr);
//                    Bitmap bmin = BitmapFactory.decodeByteArray(byteArr,0,byteArr.length);
//                    Bitmap bmout = tools.getBitmapOrientationAdjusted(bmin);
//                    mBitmapList.add(bmout);

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @NonNull
        @Override
        public ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ImageGalleryActivity.this)
                    .inflate(R.layout.galleryitem_layout,parent,false);

            ImageVH vh = new ImageVH(view);
            //vh.imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_img_placeholder));
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ImageVH holder, final int position) {

            final ImageFromFireBase mImage = imageneslista.get(position);
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //final byte[] imgbyte = Base64.decode(mImage.getImg_string(),Base64.DEFAULT);
            //mBitmapList.get(position).compress(Bitmap.CompressFormat.JPEG,70,baos);
            //final byte[] byteArr = baos.toByteArray();
            //Bitmap imgItemBitmap = BitmapFactory.decodeByteArray(imgbyte,0,imgbyte.length);
            //Bitmap rotatedBM = tools.getBitmapOrientationAdjusted(imgItemBitmap);
            //final Bitmap rotatedBM = mBitmapList.get(position);
            Glide.with(ctx).load(mImage.getImgStoragePath())
                    //.placeholder(R.drawable.ic_img_placeholder)
                    .apply(new RequestOptions().centerCrop())
                    //.transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{

                        //dialogoImagePreview();
                        startActivity(new Intent(ctx, ImagePreview.class)
                                .putExtra("email", mImage.getEmail())
                                .putExtra("psw", mImage.getPassword())
                                .putExtra("nombre", mImage.getNombre())
                                .putExtra("fromInicio",fromInicio)
                                //.putExtra("img_str", mImage.getImg_string())
                                //.putExtra("BM",rotatedBM)
                                //.putExtra("byteArrImg",mByteArrList.get(position))
                                .putExtra("imgStoragePath",mImage.getImgStoragePath())
                                .putExtra("fromCamera",false)
                        );
                    }catch (Exception e){e.printStackTrace();}
                }
            });
        }

        public void dialogoImagePreview(){
            try{

                Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.dialogo_img_preview);
                ImageView imageView = dialog.findViewById(R.id.imagePreviewFullSize);
                FloatingActionButton btnBack = dialog.findViewById(R.id.btnBack);
                FloatingActionButton fabDeleteImg = dialog.findViewById(R.id.fabDeleteImg);


            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return imageneslista.size();
        }

        class ImageVH extends RecyclerView.ViewHolder{

            //LinearLayout linearLayout;
            ImageView imageView;
            public ImageVH(@NonNull View itemView) {
                super(itemView);
                //linearLayout = itemView.findViewById(R.id.linearItemGallery);
                imageView = itemView.findViewById(R.id.imgItemGallery);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
    }
}
