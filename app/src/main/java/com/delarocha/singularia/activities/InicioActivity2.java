package com.delarocha.singularia.activities;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.delarocha.singularia.auxclasses.ContentFragment;
import com.delarocha.singularia.auxclasses.Tools;
import com.delarocha.singularia.camera.SingulariaCam;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class InicioActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Context context = this;
    private String TAG = "InicioActivity2";
    private DocumentReference dRef;
    private Account account;
    private NavigationView navigationView;
    private LinearLayout pagesIndicator;
    private DrawerLayout drawer;
    private ArrayList<Fragment> fragmentList;
    public List<Map<String,Object>> mapList;
    private FirebaseFirestore firestoreDb;
    private List<String> imageURLList;
    private List<Integer> imageIdList;
    private Tools mTools;
    private String email, psw, username,avatar_img_str;
    public Handler handler;
    private Runnable update;
    private int itemCount = 0;
    private TextView barTextCount;
    private CircleImageView imageUser;
    private ImageView btnConfig;
    private TextView textUserName, textUserEmail;
    private View content_inicio, content_tipoInvita;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Singularia");

        Bundle extras = getIntent().getExtras();
        mTools = new Tools(context);
        email = extras.getString("email");
        psw = extras.getString("psw");
        username = extras.getString("nombre");

        itemCount = mTools.getIntPreference(Tools.ITEMCOUNT_KEYNAME);
        mTools.setStringPreference(Tools.SWIPE_DIRECTION,"");//reset swipe direcion variable
        mTools.setIntPreference(Tools.COUNT_MOVES,0);//reset moves variable
        if(itemCount==-1||itemCount==0){
            mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,0);
        }
        firestoreDb = FirebaseFirestore.getInstance();
        account = new Account();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        pagesIndicator = (LinearLayout)findViewById(R.id.pagesIndicator);
        navigationView = (NavigationView)findViewById(R.id.nav_view);


        MenuItem nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        MenuItem nav_invita = navigationView.getMenu().findItem(R.id.nav_invitaciones);
        nav_home.setVisible(false);
        nav_invita.setVisible(true);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        btnConfig = headerView.findViewById(R.id.btnConfiguration);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InicioActivity2.this,AccountConfiguration.class)
                        .putExtra("email",email)
                        .putExtra("psw", psw)
                        .putExtra("nombre",username)
                        .putExtra("img_str",avatar_img_str)
                );
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getUsuario2(email,psw);

        fragmentList = new ArrayList<>();
        imageURLList = new ArrayList<>();
        imageIdList = new ArrayList<>();
        imageIdList.add(R.drawable.promo1);
        imageIdList.add(R.drawable.promo2);
        imageIdList.add(R.drawable.promo3);
        imageIdList.add(R.drawable.promo4);
        imageIdList.add(R.drawable.promo5);
        getImagenesPromo();
//        fragmentList.add(ContentFragment.newInstance(context,"Promoción 1",0));
//        fragmentList.add(ContentFragment.newInstance(context,"Promoción 2",1));
//        fragmentList.add(ContentFragment.newInstance(context,"Promoción 3",2));
//        fragmentList.add(ContentFragment.newInstance(context,"Promoción 4",3));
//        fragmentList.add(ContentFragment.newInstance(context,"Promoción 5",4));

        mSectionsPagerAdapter = new SectionsPagerAdapter(context,fragmentList,true);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int jumpPosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    //prepare to jump to the last page
                    jumpPosition = mSectionsPagerAdapter.getListCount();

                    //TODO: indicator.setActive(adapter.getRealCount() - 1)
                } else if (position == mSectionsPagerAdapter.getListCount() + 1) {
                    //prepare to jump to the first page
                    jumpPosition = 1;

                    //TODO: indicator.setActive(0)
                } else {
                    //TODO: indicator.setActive(position - 1)
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Let's wait for the animation to be completed then do the jump (if we do this in
                //onPageSelected(int position) scroll animation will be canceled).
                if (state == ViewPager.SCROLL_STATE_IDLE && jumpPosition >= 0) {
                    //Jump without animation so the user is not aware what happened.
                    mViewPager.setCurrentItem(jumpPosition, false);
                    //Reset jump position.
                    jumpPosition = -1;
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_shopingcart_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_toolbar);
        MenuItemCompat.setActionView(menuItem,R.layout.shop_cart_count);
        RelativeLayout rl = (RelativeLayout)MenuItemCompat.getActionView(menuItem);
        barTextCount = (TextView)rl.findViewById(R.id.count_textview);
        firestoreDb.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
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
        barTextCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch(id){
            case R.id.nav_invitaciones:
                startActivity(new Intent(InicioActivity2.this,TipoInvitaActivity.class)
                        .putExtra("email", email)
                        .putExtra("psw", psw)
                        .putExtra("nombre",username)
                        .putExtra("img_str",avatar_img_str)
                );
                break;
            case R.id.nav_pub_escrita:
                Intent intent = new Intent(InicioActivity2.this, PubEscritaActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_view_souv:
                //startActivity(new Intent(InicioActivity.this, PlayerasActivity.class));
                break;
            //case R.id.nav_configuracion:
            //break;
            case R.id.nav_camera:
                startActivity(new Intent(InicioActivity2.this, SingulariaCam.class)
                                .putExtra("email", email)
                                .putExtra("psw", psw)
                                .putExtra("nombre",username)
                        //.putExtra("img_str",img_str)
                );
                break;
            case R.id.nav_galeria:
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends LoopingPagerAdapter<Fragment> {

//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }

        public SectionsPagerAdapter(Context context, ArrayList<Fragment> itemList, boolean isInfinite){
            super(context, itemList, isInfinite);
        }



        @Override
        protected View inflateView(int viewType, ViewGroup container, int listPosition) {

            return LayoutInflater.from(context).inflate(R.layout.fragment_content,container,false);
        }

        @Override
        protected void bindView(View convertView, int listPosition, int viewType) {
            TextView text = convertView.findViewById(R.id.text);
            ImageView img = convertView.findViewById(R.id.img_promo);

            if(imageURLList.size()!=0){
                Picasso.with(context).load(imageURLList.get(listPosition)).fit().into(img);
            }else{
                img.setImageResource(imageIdList.get(listPosition));
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public void getImagenesPromo(){
        mapList = new ArrayList<>();
        try{
            firestoreDb.collection(Tools.FIRESTORE_IMGS_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DOCUMENT", document.getId() + " => " + document.getData());
                                    mapList.add(document.getData());
                                }
                                setImageList();
                            } else {
                                Log.d("DOCUMENT", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setImageList(){

        for(Map<String, Object> map: mapList){
            String imgUrl0 = map.get("imgPromo0").toString();
            String imgUrl1 = map.get("imgPromo1").toString();
            String imgUrl2 = map.get("imgPromo2").toString();
            String imgUrl3 = map.get("imgPromo3").toString();
            String imgUrl4 = map.get("imgPromo4").toString();

            imageURLList.add(imgUrl0);
            imageURLList.add(imgUrl1);
            imageURLList.add(imgUrl2);
            imageURLList.add(imgUrl3);
            imageURLList.add(imgUrl4);
        }
    }

    private void resetInvitaOnShopCart(){
        try{
            firestoreDb.collection(Tools.FIRESTORE_INVITACIONES_COLLECTION)
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
            e.printStackTrace();
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

    private void getUsuario2(String email, String psw){
        try{
            byte[] decodedString = new byte[1];
            Bitmap userBitmap = null;
            dRef = firestoreDb.collection(Tools.FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
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
                        avatar_img_str = account.getImg_string();
                        textUserName = (TextView)findViewById(R.id.textUserName);
                        imageUser = (CircleImageView)findViewById(R.id.imageUser);
                        textUserEmail = (TextView) findViewById(R.id.textUserEmail);
                        textUserName.setText(account.getNombre());
                        textUserEmail.setText(account.getEmail());
                        if(!avatar_img_str.equals("")){
                            byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                            Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageUser.setImageBitmap(userBitmap);
                        }else{
                            imageUser.setBackground(getResources().getDrawable(R.drawable.ic_user));
                        }
                        mTools.setStringPreference("user",account.getNombre());
                        mTools.setStringPreference("email",account.getEmail());
                        mTools.setStringPreference("img_str",account.getImg_string());
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

    @Override
    protected void onResume() {
        super.onResume();
        //mViewPager.resumeAutoScroll();
    }

    @Override
    protected void onPause() {
        //mViewPager.pauseAutoScroll();
        super.onPause();
    }
}
