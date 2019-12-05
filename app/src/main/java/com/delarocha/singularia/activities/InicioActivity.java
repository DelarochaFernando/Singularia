package com.delarocha.singularia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.ContentFragment;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.auxclasses.ViewPagerCustomScrollSpeed;
import com.delarocha.singularia.camera.SingulariaCam;
import com.delarocha.singularia.pageindicator.LoopingPagerAdapter;
import com.delarocha.singularia.pageindicator.MyPageIndicator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

//import androidx.viewpager2.adapter.FragmentStateAdapter;
//import androidx.viewpager2.widget.ViewPager2;

//import android.support.annotation.NonNull;
//import android.support.design.widget.NavigationView;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;

public class InicioActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, Serializable {

    //private static final String FIRESTORE_ACCOUNTS_COLLECTION = "accounts";
    private Context context = this;
    private String email, psw, username, avatar_img_str,avatar_storage_path,pregSeg, resSeguridad, uid;
    private FirebaseFirestore DBfirebase;
    private String TAG = "InicioActivity";
    private DocumentReference dRef;
    private CollectionReference collRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Account account;
    private NavigationView navigationView, nav_footer;
    private DrawerLayout drawer;
    //private FloatingActionButton fab;
    private CircleImageView imageUser;
    private ImageView btnConfig;
    private TextView textUserName, textUserEmail;
    //private ViewPager container,view_pager;
    private ViewPagerCustomScrollSpeed view_pager;
    private RecyclerView recyclerPromos;
    //private ViewPager2 view_pager2;
    private LinearLayout pagesIndicator;
    private PromosPagerAdapter promosPagerAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private MyPageIndicator myIndicator;
    private Toolbar toolbar;
    private List<Fragment> fragmentList;
    public Handler handler;
    private Runnable update;
    private Tools mTools;
    private int itemCount = 0;
    private TextView barTextCount;
    private ImageView imgIconShopCart;
    private View content_inicio, content_tipoInvita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Singularia");

        Bundle extras = getIntent().getExtras();
        mTools = new Tools(context);
        email = extras.getString("email");
        psw = extras.getString("psw");
        username = extras.getString("nombre");
        pregSeg = extras.getString("pregSeguridad");
        resSeguridad = extras.getString("resSeguridad");

        itemCount = mTools.getIntPreference(Tools.ITEMCOUNT_KEYNAME);
        mTools.setStringPreference(Tools.SWIPE_DIRECTION,"");//reset swipe direcion variable
        mTools.setIntPreference(Tools.COUNT_MOVES,0);//reset moves variable
        if(itemCount==-1||itemCount==0){
            mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,0);
        }

        DBfirebase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        account = new Account();

        fragmentList = new ArrayList<>();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //recyclerPromos = findViewById(R.id.recyclerPromos);

        view_pager = (ViewPagerCustomScrollSpeed)findViewById(R.id.view_pager);
        //pagesIndicator = (LinearLayout)findViewById(R.id.pagesIndicator);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //nav_footer = findViewById(R.id.nav_footer);
        MenuItem nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        MenuItem nav_invita = navigationView.getMenu().findItem(R.id.nav_invitaciones);
        MenuItem nav_souvenir = navigationView.getMenu().findItem(R.id.nav_souvenirs);
        nav_home.setVisible(false);
        nav_invita.setVisible(true);
        nav_souvenir.setVisible(true);
        navigationView.setNavigationItemSelectedListener(this);
        //nav_footer.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        btnConfig = headerView.findViewById(R.id.btnConfiguration);


        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InicioActivity.this,AccountConfiguration.class)
                .putExtra("email",email)
                .putExtra("psw", psw)
                .putExtra("nombre",username)
                .putExtra(Tools.FROM_TIPOINVITA_TAG,false)
                //.putExtra("img_str",avatar_img_str)
                .putExtra("pregSeguridad",pregSeg)
                .putExtra("resSeguridad",resSeguridad)
                );
            }
        });



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getUsuario2(email,psw);
        getPromosInicio();
        //textUserName.setText(account.getNombre()+" "+ account.getLastname());
        //textUserEmail.setText(account.getEmail());
        /*try{
            byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
            Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageUser.setImageBitmap(userBitmap);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);

        //fragmentList.add(ContentFragment.newInstance(context,"Promoción 1",0));
        //fragmentList.add(ContentFragment.newInstance(context,"Promoción 2",1));
        //fragmentList.add(ContentFragment.newInstance(context,"Promoción 3",2));
        //fragmentList.add(ContentFragment.newInstance(context,"Promoción 4",3));
        //fragmentList.add(ContentFragment.newInstance(context,"Promoción 5",4));
        //fragmentList.add(ContentFragment.newInstance("Promoción 6",5));
        promosPagerAdapter = new PromosPagerAdapter(getSupportFragmentManager(), fragmentList);
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            float tempPositionOffset = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    if (tempPositionOffset < positionOffset) {
                        Log.d("eric", "scrolling left ...");
                    } else {
                        Log.d("eric", "scrolling right ...");
                    }

                    tempPositionOffset = positionOffset;

                    Log.d("eric", "position " + position + "; " + " positionOffset " + positionOffset + "; " + " positionOffsetPixels " + positionOffsetPixels + ";");
                }
            }

            @Override
            public void onPageSelected(int position) {
                int index = position % fragmentList.size();
                Log.i("VIEWPAGER page selected"," "+String.valueOf(position));
                Log.i("INDEX & CURRENTPAGE"," INDEX: "+String.valueOf(index)+"| CURRENTPAGE: "+String.valueOf(currentPage));
                String movimiento = mTools.getStringPreference(Tools.SWIPE_DIRECTION);
                //if(movimiento.equals("derecha")){ index = index - 1; }
                //if(movimiento.equals("izquierda")){index = index +1;}
                //myIndicator.setIndicatorAsSelected(index);
                //index = position % fragmentList.size();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state){
                    case ViewPager.SCROLL_STATE_IDLE:
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                }

            }
        };
        view_pager.setOnPageChangeListener(onPageChangeListener);
        //view_pager.setAdapter(promosPagerAdapter);
//        view_pager.setScrollDuration(5000);

        view_pager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Haz lanzado actividad de esta promoción!!!", Toast.LENGTH_SHORT).show();
            }
        });

//        myIndicator = new MyPageIndicator(this, pagesIndicator, view_pager, R.drawable.indicator_circle,onPageChangeListener);
        //myIndicator = new MyPageIndicator(this, pagesIndicator, view_pager3, R.drawable.indicator_circle,onPageChangeListener);
//        myIndicator.setPageCount(fragmentList.size());
//        myIndicator.show();
//        setUpAutoScrolling();
    }

    private void getUsuario2(String email, String psw){
        try{
            byte[] decodedString = new byte[1];
            Bitmap userBitmap = null;
            dRef = DBfirebase.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
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
                        avatar_storage_path = account.getImg_storage_path();
                        textUserName = (TextView)findViewById(R.id.textUserName);
                        imageUser = (CircleImageView)findViewById(R.id.imageUser);
                        textUserEmail = (TextView) findViewById(R.id.textUserEmail);
                        textUserName.setText(account.getNombre());
                        textUserEmail.setText(account.getEmail());
                        if(!avatar_storage_path.equals("")){
                            //byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                            //Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(context).load(avatar_storage_path).into(imageUser);
                            //imageUser.setImageBitmap(userBitmap);
                        }else{
                            imageUser.setBackground(getResources().getDrawable(R.drawable.ic_default_user_white));
                        }
                        mTools.setStringPreference("user",account.getNombre());
                        mTools.setStringPreference("email",account.getEmail());
                        mTools.setStringPreference("img_str",account.getImg_string());
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

    private void getPromosInicio(){

        DBfirebase.collection(Tools.FIRESTORE_IMGS_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                       for(int i = 0; i<task.getResult().getDocuments().size();i++){
                           DocumentSnapshot docSnap = task.getResult().getDocuments().get(i);
                           String imgstr = docSnap.getString("imgPromoString");
                           String texto = docSnap.getString("textoPromo");
                           fragmentList.add(ContentFragment.newInstance(context,imgstr,texto,i));
                       }

                        promosPagerAdapter = new PromosPagerAdapter(getSupportFragmentManager(), fragmentList);
                        view_pager.setAdapter(promosPagerAdapter);
                    }
                  }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

    }

    private void getUsuario(String email, String psw) {
        try{
            dRef = DBfirebase.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
            dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //user[0] = new Usuario();
                    //user[0].setNombre(documentSnapshot.getString("nombre"));
                    //user[0].setPassword(documentSnapshot.getString("password"));
                    if(documentSnapshot.exists()){
                        account = documentSnapshot.toObject(Account.class);
                        //textUserName.setText(account.getNombre()+" "+ account.getLastname());
                        textUserEmail.setText(account.getEmail());
                        try{
                            byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                            Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageUser.setImageBitmap(userBitmap);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        /*usuario.setNombre(documentSnapshot.getString("nombre"));
                        usuario.setLastname(documentSnapshot.getString("lastname"));
                        usuario.setSurname(documentSnapshot.getString("surname"));
                        usuario.setPassword(documentSnapshot.getString("password"));
                        usuario.setEmail(documentSnapshot.getString("email"));
                        usuario.setImg_string(documentSnapshot.getString("img_string"));*/
                    }else{

                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.inicio, menu);
        getMenuInflater().inflate(R.menu.toolbar_shopingcart_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_toolbar);
        MenuItemCompat.setActionView(menuItem,R.layout.shop_cart_count);
        RelativeLayout rl = (RelativeLayout)MenuItemCompat.getActionView(menuItem);
        barTextCount = (TextView)rl.findViewById(R.id.count_textview);
        imgIconShopCart = rl.findViewById(R.id.imgIconShopCart);

        DBfirebase.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
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
                                if(docSnapshotList.get(i).getId().contains(uid)){
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
                        .putExtra("fromInicio", true)
                        .putExtra("activityFrom",InicioActivity.class)
                );
            }
        });
        barTextCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,ShopCartActivity.class)
                        .putExtra("email",email)
                        .putExtra("psw",psw)
                        .putExtra("nombre",username)
                        .putExtra("resSeguridad",resSeguridad)
                        .putExtra("pregSeguridad",pregSeg)
                        .putExtra("fromInicio", true)
                        .putExtra("activityFrom",InicioActivity.class)
                );
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_invitaciones:
                startActivity(new Intent(InicioActivity.this,TipoInvitaActivity.class)
                .putExtra("email", email)
                .putExtra("psw", psw)
                .putExtra("nombre",username)
                .putExtra("pregSeguridad",pregSeg)
                .putExtra("resSeguridad",resSeguridad)
                .putExtra("fromInicio",true)
                //.putExtra("img_str",avatar_img_str)
                );
                break;
            case R.id.nav_pub_escrita:

                startActivity(new Intent(InicioActivity.this, PubEscritaActivity.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                                .putExtra("pregSeguridad",pregSeg)
                                .putExtra("resSeguridad",resSeguridad)
                                .putExtra("fromInicio",true)
                        //.putExtra("img_str",img_str)
                );
                break;
            case R.id.nav_souvenirs:
                //startActivity(new Intent(InicioActivity.this, PlayerasActivity.class));
                startActivity(new Intent(InicioActivity.this, Souvenirs.class)
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
                startActivity(new Intent(InicioActivity.this, SingulariaCam.class)
                        .putExtra("email", email)
                        .putExtra("psw", psw)
                        .putExtra("nombre",username)
                        .putExtra("pregSeguridad",pregSeg)
                        .putExtra("resSeguridad",resSeguridad)
                        .putExtra("fromInicio",true)
                        //.putExtra("img_str",img_str)
                );
                break;
            case R.id.nav_galeria:
                startActivity(new Intent(InicioActivity.this, ImageGalleryActivity.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                                .putExtra("pregSeguridad",pregSeg)
                                .putExtra("resSeguridad",resSeguridad)
                                .putExtra("fromInicio",true)
                                //.putExtra("nombre",username)
                        //.putExtra("img_str",img_str)
                );
                break;
            case R.id.nav_regalos:

                break;

//            case R.id.nav_share:
//                break;
//
//            case R.id.nav_send:
//                break;
            case R.id.nav_exit:
                showClosingDialog();
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void resetInvitaOnShopCart(){
        try{
            DBfirebase.collection(Tools.FIRESTORE_INVITACIONES_COLLECTION)
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
                        mTools.setBooleanPreference(Tools.USER_LOOGED_IN_STATUS_KEYNAME,false);
                        mTools.setStringPreference(Tools.USER_LOGIN_EMAIL_KEYNAME,null);
                        mTools.setStringPreference(Tools.USER_LOGIN_PSW_KEYNAME,null);
                        startActivity(new Intent(context, LoginActivity.class)
                        .putExtra("newPsw", false)
                        );
                        System.gc();
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

    /*public static class PlaceholderPromosFragment extends Fragment {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderPromosFragment() {
        }

        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
        public static PlaceholderPromosFragment newInstance(int sectionNumber) {
            PlaceholderPromosFragment fragment = new PlaceholderPromosFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_inicio_activity2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }*/

    int currentPage = 0;
    public void setUpAutoScrolling(){
        handler = new Handler();
        update = new Runnable() {
            @Override
            public void run() {
                String movimiento = mTools.getStringPreference(Tools.SWIPE_DIRECTION);
                if(movimiento.equals("")){
                    view_pager.setCurrentItem(currentPage,true);
                    if(currentPage == Integer.MAX_VALUE){
                        currentPage = 0;
                    //}else if(movimiento.equals("derecha")){
                        //--currentPage;
                        //view_pager.setCurrentItem(currentPage,true);
                        //mTools.setStringPreference(Tools.SWIPE_DIRECTION,"");
                    } else{
                        ++currentPage;
                    }
                }else if(movimiento.equals("derecha")){
                    --currentPage;
                    //view_pager.setCurrentItem(currentPage,true);
                    mTools.setStringPreference(Tools.SWIPE_DIRECTION,"");
                }else if(movimiento.equals("izquierda")){
                    ++currentPage;
                    mTools.setStringPreference(Tools.SWIPE_DIRECTION,"");
                }
                /*if(currentPage == Integer.MAX_VALUE){
                    currentPage = 0;
                }else if(movimiento.equals("derecha")){
                    --currentPage;
                    view_pager.setCurrentItem(currentPage,true);
                    mTools.setStringPreference(Tools.SWIPE_DIRECTION,"");
                } else{
                    ++currentPage;
                }*/
            }
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        },2000,3500);
    }

    /*private class PromosCardsAdapter extends RecyclerView.Adapter<PromosCardsAdapter.PromosVH>{

        public PromosCardsAdapter(List<>){

        }

        @NonNull
        @Override
        public PromosCardsAdapter.PromosVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.promo_item,parent,false);
            PromosVH promosvh = new PromosVH(v);
            return promosvh;
        }

        @Override
        public void onBindViewHolder(@NonNull PromosCardsAdapter.PromosVH holder, int position) {

        }

        @Override
        public int getItemCount() {
            return promosList.size();
        }

        class PromosVH extends RecyclerView.ViewHolder{

            //String imgStr;
            //String promTexto;
            TextView txtProm;
            CardView cardProm;
            ImageView imgVProm;
            public PromosVH(@NonNull View itemView) {
                super(itemView);
                txtProm = itemView.findViewById(R.id.txtProm);
                cardProm = itemView.findViewById(R.id.cardProm);
                imgVProm = itemView.findViewById(R.id.imgVProm);
            }
        }
    }*/

    public class PromosPagerAdapter extends FragmentStatePagerAdapter implements LoopingPagerAdapter {

        List<Fragment> mFrags = new ArrayList<>();
        FragmentManager mFragMan;
        public PromosPagerAdapter(FragmentManager fm, List<Fragment> fraglist) {
            super(fm);
            mFragMan = fm;
            mFrags = fraglist;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderPromosFragment.newInstance(position + 1);
            int index = position%mFrags.size();
            return mFrags.get(index);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return Integer.MAX_VALUE;
        }

        @Override
        public int getRealCount() {
            return mFrags.size();
        }
    }

    String[] urlImagenesPromo = new String[]{
            "http://2.bp.blogspot.com/-nVz5MF6osfg/UFulGxGcwqI/AAAAAAAAAJU/1KW9HDH0aBg/s1600/dctomujerface.jpg",
            "http://www.cocinasriviera.com.mx/images/25.png",
            "https://thumbs.dreamstime.com/b/%D1%87%D0%B5%D1%80%D0%BD%D1%8B%D0%B5-%D1%86%D0%B5%D0%BD%D0%BD%D0%B8%D0%BA%D0%B8-%D0%B2%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B0-%D0%BF%D1%80%D0%BE-%D0%B0%D0%B6%D0%B8-%D0%BF%D1%8F%D1%82%D0%BD%D0%B8%D1%86%D1%8B-%D0%B2%D0%B8%D1%81%D1%8F-%D0%B2-%D0%B1%D0%B5-%D0%BE%D0%B9-%D0%BF%D1%80%D0%B5-%D0%BF%D0%BE%D1%81%D1%8B-%D0%BA%D0%B5-78615893.jpg",
            "https://thumbs.dreamstime.com/b/precios-negros-del-vector-de-la-venta-viernes-para-las-promociones-descuento-con-dise%C3%B1os-aislados-en-el-fondo-blanco-103849089.jpg",
            "https://liquidazona.com/wp-content/uploads/2018/12/1-83.jpg"
    };


}
