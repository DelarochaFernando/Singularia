package com.delarocha.singularia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delarocha.singularia.R;
import com.delarocha.singularia.adapter.TipoInvitaAdapter;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.TipoInvitaCard;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.camera.SingulariaCam;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import de.hdodenhof.circleimageview.CircleImageView;

//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;

public class TipoInvitaActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,Serializable{

    private static final String TAG = "TipoInvitaActivity";
    public static String URL_IMAGE_BODA = "www.lomejorenbodas.com/wp-content/uploads/2016/12/Boda-catolica.jpg";
    public static String URL_IMAGE_PROM = "https://www.elsoldepuebla.com.mx/incoming/vema4b-graduacion.jpg/ALTERNATES/LANDSCAPE_1140/graduacion.jpg";

    private RecyclerView recyclerTipoInvita;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView imageView;
    private TextView textView, barTextCount;
    private TextView UserName, UserEmail;
    private DocumentReference dRef;
    //private AppCompatTextView UserName, UserEmail;
    private TipoInvitaAdapter mTipoInvitaAdapter;
    private TipoInvitaCard mTipoInvitaCard;
    private ArrayList<TipoInvitaCard> mTipoInvitaList;
    private FirebaseFirestore mFireStoreDB;
    private NavigationView navigationView;
    private DrawerLayout drawer_invita;
    private CircleImageView image_User;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    String email, psw, username, pregSeg,resSeguridad, avatar_img_str;
    private Context context = this;
    private View content_inicio, content_tipoInvita;
    private Account account;
    private String avatar_img_storage_path;
    private boolean fromInicio;
    private ImageView imgIconShopCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipoinvita);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Invitaciones");

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        psw = extras.getString("psw");
        username = extras.getString("nombre");
        pregSeg = extras.getString("pregSeguridad");
        resSeguridad = extras.getString("resSeguridad");
        fromInicio = extras.getBoolean("fromInicio");
        //avatar_img_str = extras.getString("img_str");
        mFireStoreDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        prepareCards();
        getUsuario2(email,psw);
        drawer_invita = (DrawerLayout)findViewById(R.id.drawer_tipo_invita);
        navigationView = (NavigationView)findViewById(R.id.nav_view_tipoInvita);
        MenuItem nav_invitaciones = navigationView.getMenu().findItem(R.id.nav_invitaciones);
        MenuItem nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_invitaciones.setVisible(false);
        nav_home.setVisible(true);

        View header = navigationView.getHeaderView(0);
        //UserName = (TextView) header.findViewById(R.id.textUserName_TipoInvita);
        //image_User = (CircleImageView)header.findViewById(R.id.imageUser_TipoInvita);
        //UserEmail = (TextView) header.findViewById(R.id.textUserEmail_TipoInvita);
        recyclerTipoInvita = (RecyclerView)findViewById(R.id.recyclerTipoInvita);
        //UserName = (TextView)header.findViewById(R.id.textUserName);
        //image_User = (CircleImageView)header.findViewById(R.id.imageUser);
        //UserEmail = (TextView) header.findViewById(R.id.textUserEmail);
        ImageView btnConfiguration = header.findViewById(R.id.btnConfiguration);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_invita, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_invita.setDrawerListener(toggle);
        toggle.syncState();

        btnConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,AccountConfiguration.class)
                        .putExtra("email",email)
                        .putExtra("psw", psw)
                        .putExtra(Tools.FROM_TIPOINVITA_TAG,true)
                        .putExtra("nombre",username)
                        //.putExtra("img_str",avatar_img_str)
                );
            }
        });

        byte[] decodedString = new byte[1];


        mTipoInvitaAdapter = new TipoInvitaAdapter(this,mTipoInvitaList,extras);
        recyclerTipoInvita.setAdapter(mTipoInvitaAdapter);
        //mLayoutManager = new GridLayoutManager(this,2);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerTipoInvita.setLayoutManager(mLayoutManager);
        recyclerTipoInvita.hasFixedSize();

        //UserName.setText(username);
        //UserEmail.setText(email);
        /*if(!avatar_img_str.equals("")){
            decodedString = Base64.decode(avatar_img_str, Base64.DEFAULT);
            Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(context).load(userBitmap).into(image_User);
            //image_User.setImageBitmap(userBitmap);
        }else{
            image_User.setBackground(getResources().getDrawable(R.drawable.ic_user));
        }*/

        //Picasso.with(this).load(URL_IMAGE_PROM).resize(250,250).into(imageView);
        //Picasso.with(this).setLoggingEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if(drawer_invita.isDrawerOpen(GravityCompat.START)){
            drawer_invita.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void getUsuario2(String email, String psw){
        try{
            byte[] decodedString = new byte[1];
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
                        avatar_img_str = account.getImg_string();
                        avatar_img_storage_path = account.getImg_storage_path();
                        UserName = findViewById(R.id.textUserName);
                        image_User = findViewById(R.id.imageUser);
                        UserEmail = findViewById(R.id.textUserEmail);
                        UserName.setText(account.getNombre());
                        UserEmail.setText(account.getEmail());
                        if(!avatar_img_storage_path.equals("")){
                            //byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                            //Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(context).load(avatar_img_storage_path).into(image_User);
                        }else{
                            image_User.setBackground(getResources().getDrawable(R.drawable.ic_default_user_white));
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_shopingcart_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_toolbar);
        MenuItemCompat.setActionView(menuItem,R.layout.shop_cart_count);
        RelativeLayout rl = (RelativeLayout)MenuItemCompat.getActionView(menuItem);
        barTextCount = (TextView)rl.findViewById(R.id.count_textview);
        imgIconShopCart = rl.findViewById(R.id.imgIconShopCart);
        mFireStoreDB.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if(e!=null){
                            return;
                        }
                        if(queryDocumentSnapshots!=null){
                            List<DocumentSnapshot> docSnapshotList = new ArrayList<>();
                            List<DocumentSnapshot> docSnapshotListFiltered = new ArrayList<>();
                            docSnapshotList = queryDocumentSnapshots.getDocuments();
                            for(int i = 0; i<docSnapshotList.size();i++){
                                if(docSnapshotList.get(i).getId().contains(email)){
                                    docSnapshotListFiltered.add(docSnapshotList.get(i));
                                    barTextCount.setText(String.valueOf(docSnapshotListFiltered.size()));
                                }
                            }
                        }
                    }
                });
        imgIconShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,ShopCartActivity.class)
                        .putExtra("email",email)
                        .putExtra("psw",psw)
                        .putExtra("nombre",username)
                        .putExtra("resSeguridad",resSeguridad)
                        .putExtra("pregSeguridad",pregSeg)
                        .putExtra("fromInicio", false)
                        .putExtra("activityFrom",TipoInvitaActivity.class)
                );
            }
        });
        barTextCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
        /*switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this,InicioActivity.class)
                 .putExtra("email", email)
                 .putExtra("psw", psw));
            break;
        }*/
        //return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch(id){
            case R.id.nav_home:
                //This is the current Activity
                startActivity(new Intent(TipoInvitaActivity.this,InicioActivity.class)
                .putExtra("email", email)
                .putExtra("psw", psw)
                .putExtra("nombre",username)
                //.putExtra("img_str",avatar_img_str)
                );
                break;
            case R.id.nav_pub_escrita:
                startActivity(new Intent(TipoInvitaActivity.this,PubEscritaActivity.class)
                        .putExtra("email", email)
                        .putExtra("psw", psw)
                        .putExtra("nombre",username)
                        //.putExtra("img_str",avatar_img_str)
                );
                break;
            case R.id.nav_souvenirs:
                startActivity(new Intent(TipoInvitaActivity.this, Souvenirs.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                                .putExtra("pregSeguridad",pregSeg)
                                .putExtra("resSeguridad",resSeguridad)
                                .putExtra("fromInicio",true)
                        //.putExtra("img_str",img_str)
                );
                break;
            //case R.id.nav_configuracion:
                //break;
            case R.id.nav_camera:
                fromInicio = false;
                startActivity(new Intent(TipoInvitaActivity.this, SingulariaCam.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                                .putExtra("pregSeguridad",pregSeg)
                                .putExtra("resSeguridad",resSeguridad)
                                .putExtra("fromInicio",fromInicio)
                        //.putExtra("img_str",img_str)
                );
                break;
            case R.id.nav_galeria:
                fromInicio = false;
                startActivity(new Intent(TipoInvitaActivity.this, ImageGalleryActivity.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                                .putExtra("pregSeguridad",pregSeg)
                                .putExtra("resSeguridad",resSeguridad)
                                .putExtra("fromInicio",fromInicio)
                        //.putExtra("nombre",username)
                        //.putExtra("img_str",img_str)
                );
                break;

//            case R.id.nav_share:
//                break;
//
//            case R.id.nav_send:
//                break;
            case R.id.nav_exit:
                showClosingDialog();
        }
        drawer_invita.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showClosingDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Cerrar Sesión.")
                .setIcon(R.drawable.ic_alert)
                .setMessage("¿Estas seguro de cerrar la sesión?")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetInvitaOnShopCart();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(context, LoginActivity.class)
                        .putExtra("newPsw", false)
                        );
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
        dialog.show();
    }

    private void resetInvitaOnShopCart(){
        try{
            mFireStoreDB.collection(Tools.FIRESTORE_INVITACIONES_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    //mapList.add(document.getData());
                                    //Map<String,Object> map = document.getData();
                                    //map.
                                    if(document.getData().containsKey("onShopCar")){

                                        Task<Void> reference = document.getReference()
                                                .update("onShopCar",false)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "TABLE: "+Tools.FIRESTORE_INVITACIONES_COLLECTION+" resetted.");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "TABLE: "+Tools.FIRESTORE_INVITACIONES_COLLECTION+" resetted.");
                                                    }
                                                });
                                    }
                                }
                                //getSelectedItems();
                                //InitialSetUp();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }catch (Exception e){

        }

    }

    public void prepareCards(){

        mTipoInvitaList = new ArrayList<TipoInvitaCard>();
        int[] cardImage = new int[]{
        R.drawable.image_boda,
        R.drawable.image_babyshower,
        R.drawable.image_bautizo,
        R.drawable.image_fiesta_infantil,
        R.drawable.image_xv,
        R.drawable.image_graduacion,
        R.drawable.image_fiesta_tematica,
        R.drawable.image_despedida_soltera
        };

        String[] tituloTipoInvita = new String[]{
                "Boda",
                "Baby Shower",
                "Bautizo",
                "Fiestas Infantiles",
                "XV años",
                "Graduacion",
                "Fiesta Temática",
                "Despedida de Soltero(a)"
        };

        String[] arrayUrlImage = new String[]{
            "https://www.lomejorenbodas.com/wp-content/uploads/2016/12/Boda-catolica.jpg",
            "https://i1.wp.com/maternidadfacil.com/wp-content/uploads/2016/04/baby-shower3.jpg?fit=1024%2C1024&ssl=1",
            "https://www.loscincoenebros.com/wp-content/uploads/2018/03/organizar-un-bautizo.jpg",
            "https://static.vix.com/es/sites/default/files/styles/large/public/imj/hogartotal/d/decoraciones-con-globos-para-fiestas-infantiles-1.jpg?itok=3ICV8iJ_",
            "https://static.vix.com/es/sites/default/files/styles/large/public/imj/elgrancatador/i/ideas-de-tragos-para-cumple-de-quince-1.jpg?itok=YfT4i92k",
            "https://www.elsoldepuebla.com.mx/incoming/vema4b-graduacion.jpg/alternates/LANDSCAPE_768/graduacion.jpg",
            "http://www.laduendeneta.com/fiestas/1Flower.jpg",
            "https://media.istockphoto.com/photos/bride-having-fun-on-her-bachelorette-party-picture-id491974030?k=6&m=491974030&s=612x612&w=0&h=8j-eGP0dymN_n9D-aUW2-NDYrV_HRDV11Yk1zgmVjCk="
        };
        for(int i = 0; i < cardImage.length;i++){
            TipoInvitaCard tic = new TipoInvitaCard(tituloTipoInvita[i],cardImage[i], arrayUrlImage[i]);
            mTipoInvitaList.add(tic);
        }
    }
}
