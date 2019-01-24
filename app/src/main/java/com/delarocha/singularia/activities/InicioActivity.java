package com.delarocha.singularia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.ContentFragment;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.auxclasses.ViewPagerCustomScrollSpeed;
import com.delarocha.singularia.pageindicator.LoopingPagerAdapter;
import com.delarocha.singularia.pageindicator.MyPageIndicator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class InicioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //private static final String FIRESTORE_ACCOUNTS_COLLECTION = "accounts";
    private Context context = this;
    private String email, psw;
    private FirebaseFirestore DBfirebase;
    private DocumentReference dRef;
    private Account account;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    //private FloatingActionButton fab;
    private CircleImageView imageUser;
    private TextView textUserName, textUserEmail;
    //private ViewPager container,view_pager;
    private ViewPagerCustomScrollSpeed view_pager;
    private LinearLayout pagesIndicator;
    private PromosPagerAdapter promosPagerAdapter;
    private MyPageIndicator myIndicator;
    private Toolbar toolbar;
    private List<Fragment> fragmentList;
    private Handler handler;
    private Runnable update;
    private Tools mTools;
    private int itemCount = 0;

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

        itemCount = mTools.getIntPreference(Tools.ITEMCOUNT_KEYNAME);
        if(itemCount==-1||itemCount==0){
            mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,0);
        }

        DBfirebase = FirebaseFirestore.getInstance();
        account = new Account();

        fragmentList = new ArrayList<>();
        //fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //container = (ViewPager)findViewById(R.id.container);
        //view_pager = (ViewPager)findViewById(R.id.view_pager);
        view_pager = (ViewPagerCustomScrollSpeed)findViewById(R.id.view_pager);
        pagesIndicator = (LinearLayout)findViewById(R.id.pagesIndicator);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getUsuario2(email,psw);
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
        fragmentList.add(ContentFragment.newInstance(context,"Promoción 1",0));
        fragmentList.add(ContentFragment.newInstance(context,"Promoción 2",1));
        fragmentList.add(ContentFragment.newInstance(context,"Promoción 3",2));
        fragmentList.add(ContentFragment.newInstance(context,"Promoción 4",3));
        fragmentList.add(ContentFragment.newInstance(context,"Promoción 5",4));
        //fragmentList.add(ContentFragment.newInstance("Promoción 6",5));
        promosPagerAdapter = new PromosPagerAdapter(getSupportFragmentManager(), fragmentList);
        view_pager.setAdapter(promosPagerAdapter);
        view_pager.setScrollDuration(5000);

        myIndicator = new MyPageIndicator(this, pagesIndicator, view_pager, R.drawable.indicator_circle);
        myIndicator.setPageCount(fragmentList.size());
        myIndicator.show();
        setUpAutoScrolling();
    }

    private void getUsuario2(String email, String psw){
        try{
            dRef = DBfirebase.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        //Account acc = new Account();
                        DocumentSnapshot snapshot = task.getResult();
                        account.setNombre(snapshot.get("nombre").toString());
                        account.setEmail(snapshot.get("email").toString());
                        account.setImg_string(snapshot.get("img_string").toString());
                        textUserName = (TextView)findViewById(R.id.textUserName);
                        imageUser = (CircleImageView)findViewById(R.id.imageUser);
                        textUserEmail = (TextView) findViewById(R.id.textUserEmail);
                        textUserName.setText(account.getNombre());
                        textUserEmail.setText(account.getEmail());
                        byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                        Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageUser.setImageBitmap(userBitmap);
                        mTools.setStringPreference("user",account.getNombre());
                        mTools.setStringPreference("email",account.getEmail());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
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
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
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
                );
                break;
            case R.id.nav_pub_escrita:
                Intent intent = new Intent(InicioActivity.this, PubEscritaActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_playeras:
                //startActivity(new Intent(InicioActivity.this, PlayerasActivity.class));
                break;
            case R.id.nav_configuracion:
                break;
            case R.id.nav_camera:
                break;
            case R.id.nav_galeria:
                break;

            case R.id.nav_share:
                break;

            case R.id.nav_send:
                break;
            case R.id.nav_exit:
                showClosingDialog();
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(context, LoginActivity.class));
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
    private void setUpAutoScrolling(){
        handler = new Handler();
        update = new Runnable() {
            @Override
            public void run() {
                view_pager.setCurrentItem(currentPage,true);
                if(currentPage == Integer.MAX_VALUE){
                    currentPage = 0;
                }else{
                    ++currentPage;
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        },4000,4000);
    }

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
