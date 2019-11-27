package com.delarocha.singularia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.camera.SingulariaCam;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Souvenirs extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private Context context = this;
    private String email, psw, username, avatar_img_str,avatar_storage_path,pregSeg, resSeguridad, uid;
    private FirebaseFirestore DBfirebase;
    private String TAG = "SouvenirsActivity";
    private DocumentReference dRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFireStoreDB;
    private Account account;
    private NavigationView navigationView, nav_footer;
    private DrawerLayout drawer_souv;
    //private FloatingActionButton fab;
    private CircleImageView imageUser;
    private ImageView btnConfig;
    private TextView textUserName, textUserEmail;
    private boolean fromInicio;
    private TextView textView, barTextCount,UserName, UserEmail;
    private CircleImageView image_User;
    private ImageView imgIconShopCart;
    private String avatar_img_storage_path;
    private RecyclerView recyclerSouv;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souvenirs);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Souvenirs");

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

        getUsuario2(email,psw);
        drawer_souv = (DrawerLayout)findViewById(R.id.drawer_souv);
        navigationView = (NavigationView)findViewById(R.id.nav_view_souv);
        MenuItem nav_souvenirs = navigationView.getMenu().findItem(R.id.nav_invitaciones);
        MenuItem nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_souvenirs.setVisible(false);
        nav_home.setVisible(true);
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
                startActivity(new Intent(Souvenirs.this,InicioActivity.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                        //.putExtra("img_str",avatar_img_str)
                );
                break;
            case R.id.nav_pub_escrita:
                startActivity(new Intent(Souvenirs.this,PubEscritaActivity.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                        //.putExtra("img_str",avatar_img_str)
                );
                break;
            case R.id.nav_invitaciones:
                startActivity(new Intent(Souvenirs.this,TipoInvitaActivity.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                                .putExtra("pregSeguridad",pregSeg)
                                .putExtra("resSeguridad",resSeguridad)
                        //.putExtra("img_str",avatar_img_str)
                );
                break;
            //case R.id.nav_configuracion:
            //break;
            case R.id.nav_camera:
                fromInicio = false;
                startActivity(new Intent(Souvenirs.this, SingulariaCam.class)
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
                startActivity(new Intent(Souvenirs.this, ImageGalleryActivity.class)
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
        drawer_souv.closeDrawer(GravityCompat.START);
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
                        //resetInvitaOnShopCart();
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
}
