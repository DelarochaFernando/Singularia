package com.delarocha.singularia.activities.invita;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.activities.ShopCartActivity;
import com.delarocha.singularia.activities.TipoInvitaActivity;
import com.delarocha.singularia.adapter.InvitacionAdapter;
import com.delarocha.singularia.adapter.ProductoAdapter;
import com.delarocha.singularia.auxclasses.Invitacion;
import com.delarocha.singularia.auxclasses.ShopItem;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import android.support.annotation.NonNull;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

public class InvitaBabyShower extends AppCompatActivity {
    String email, psw,username,img_str, itemCount, uid, resSeguridad, pregSeguridad;
    RecyclerView recyclerInvitaBShower;
    InvitacionAdapter mInvitacionAdapter;
    private TextView barTextCount;
    public static String FIRESTORE_INVITACIONES_COLLECTION = "invitaciones";
    private DocumentReference docRef;
    private CollectionReference collRef;
    private FirebaseFirestore mFireStoreDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private List<Invitacion> mListaInvitaciones, mInvitasSelectedList;
    private List<String> mListaModelos;
    private List<ShopItem> shopItemsList;
    public List<Map<String, Object>> mapList = new ArrayList<>();
    public List<Map<String, Object>> mapListSelected = new ArrayList<>();
    private Invitacion invitacion;
    private int mItemSelected;
    ProductoAdapter.CustomItemClickListener itemCountCallBack;
    Tools mTools;
    private Context context = this;

    int i = 0;
    String TAG = "INVITABBSHOWER";
    private ImageView imgIconShopCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitababyshower);
        getSupportActionBar().setTitle("Baby Shower");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTools = new Tools(this);
        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        psw = extras.getString("psw");
        username = extras.getString("nombre");
        img_str = extras.getString("img_str");

        recyclerInvitaBShower = (RecyclerView) findViewById(R.id.recyclerInvitaBShower);
        mFireStoreDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        //itemCount = String.valueOf(mTools.getIntPreference(Tools.ITEMCOUNT_KEYNAME));
        getSelectedItems(new InvitaBabyShower.MyCallback() {
            @Override
            public void onGetSizeList(int selectedSize) {
                mItemSelected = selectedSize;
            }

            @Override
            public void onGetSelectedList(List<ShopItem> list) {
                shopItemsList = list;
            }

            @Override
            public void onGetSelectedInvitas(List<Invitacion> list) {
                mInvitasSelectedList = list;
            }
        });
        getInvitaciones();
    }

    private void getSelectedItems(final InvitaBabyShower.MyCallback callback) {

        final List<Invitacion> SelectedList = new ArrayList<>();
        final List<ShopItem> ShopItemList = new ArrayList<>();
        //final List<Map<String,Object>> mapListSelected = new ArrayList<>();
       // try{
            mFireStoreDB.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //barTextCount.setText(String.valueOf(task.getResult().size()));
                        //uid = mAuth.getUid();
                        for (QueryDocumentSnapshot document : task.getResult()){
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            if(document.getId().contains(uid)){
                                mapListSelected.add(document.getData());
                            }
                            //mapListSelected.add(document.getData());
                        }
                        for(Map<String, Object> map: mapListSelected){
                            boolean oferta = false;
                            boolean onShopCar = false;

                            if(map.containsKey("tieneOferta")){
                                oferta = (boolean) map.get("tieneOferta");
                            }
                            if(map.containsKey("onShopCar")){
                                onShopCar = (boolean)map.get("onShopCar");
                            }
                            //Change Invitacion object instead ShopItem Object
                            ShopItem item
                                    = new ShopItem
                                    (

                                            String.valueOf(map.get("nombreUsuario")),
                                            String.valueOf(map.get("emailUsuario")),
                                            String.valueOf(map.get("fecha")),
                                            String.valueOf(map.get("nombrePdto")),
                                            String.valueOf(map.get("imgPdto")),
                                            String.valueOf(map.get("categoriaPdto")),
                                            String.valueOf(map.get("tipoPdto")),
                                            String.valueOf(map.get("modeloPdto")),
                                            String.valueOf(map.get("cantidadPdto")),
                                            String.valueOf(map.get("comentario")),
                                            String.valueOf(map.get("precioPdto")),
                                            oferta,onShopCar
                                    );

                            Invitacion invitacion
                                    = new Invitacion
                                    (
                                            String.valueOf(map.get("nombreInvita")),
                                            String.valueOf(map.get("tipoInvita")),
                                            String.valueOf(map.get("modeloInvita")),
                                            String.valueOf(map.get("img_string")),
                                            String.valueOf(map.get("cantidad")),
                                            String.valueOf(map.get("precio")),
                                            String.valueOf(map.get("comentario")),
                                            oferta,onShopCar
                                    );

                            if(item.getTipoPdto().equals("0")){//ShopItem Invitacion Baby Shower
                                ShopItemList.add(item);
                            }
                            if(invitacion.getTipoInvita().equals("0")){//Invitacion Baby Shower
                                SelectedList.add(invitacion);
                            }
                            //ShopItemList.add(item);
                            //SelectedList.add(invitacion);
                        }
                        callback.onGetSizeList(ShopItemList.size());
                        callback.onGetSelectedList(ShopItemList);
                        callback.onGetSelectedInvitas(SelectedList);
                        //InitialSetUp();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void getInvitaciones(){
        try{
            mFireStoreDB.collection(FIRESTORE_INVITACIONES_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    mapList.add(document.getData());
                                }
                                InitialSetUp();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void InitialSetUp() {
        try{
            //      0       1           2           3                                   4                                                   5               6                           7
            //{cantidad=, precio=, tipoInvita=2, comentario=, img_string=https://cdn0.matrimonio.com.pe/usr/5/6/0/5/cfb_121932.jpg, tieneOferta=false, nombreInvita=Clasico Blanco, modeloInvita=1}
            mListaModelos = new ArrayList<>();
            List<List<Invitacion>> data = new ArrayList<>();
            List<Invitacion> modelo1 = new ArrayList<>();
            List<Invitacion> modelo2 = new ArrayList<>();

            boolean oferta = false;
            boolean onShopCar = false;
            //mListaInvitaciones = bodaList;
            mListaInvitaciones = new ArrayList<>();
            //mListaModelos.add("modelo 1");
            //mListaModelos.add("modelo 2");
            //mListaModelos.add("modelo 3");
            for(Map<String, Object> map: mapList){

                if(map.containsKey("tieneOferta")){
                    oferta = (boolean) map.get("tieneOferta");
                }
                if(map.containsKey("onShopCar")){
                    onShopCar = (boolean) map.get("onShopCar");
                }
                Invitacion invitacion
                        = new Invitacion
                        (
                                String.valueOf(map.get("nombreInvita")),
                                String.valueOf(map.get("tipoInvita")),
                                String.valueOf(map.get("modeloInvita")),
                                String.valueOf(map.get("img_string")),
                                String.valueOf(map.get("cantidad")),
                                String.valueOf(map.get("precio")),
                                String.valueOf(map.get("comentario")),
                                oferta,onShopCar
                        );
                if(invitacion.getTipoInvita().equals("0")&&invitacion.getModeloInvita().equals("1")){//Invitaciones de BabyShower
                    modelo1.add(invitacion);
                }
                if(invitacion.getTipoInvita().equals("0")&&invitacion.getModeloInvita().equals("2")){//Invitaciones de BabyShower
                    modelo2.add(invitacion);
                }
            }

            if(modelo1.size()!=0){data.add(modelo1); mListaModelos.add("Baby Shower 1");}
            if(modelo2.size()!=0){data.add(modelo2); mListaModelos.add("Baby Shower 2");}


            if(mItemSelected!=0){
                mInvitacionAdapter = new InvitacionAdapter(this,"0",mListaModelos,data,shopItemsList,itemCountCallBack);
            }else {
                mInvitacionAdapter = new InvitacionAdapter(this,"0",mListaModelos,data,itemCountCallBack);
            }

            //mInvitacionAdapter = new InvitacionAdapter(this,"2",mListaModelos,data,itemCountCallBack);
            //mInvitacionAdapter = new InvitacionAdapter(this,"0",mListaModelos,data,customItemClickListener);
            LinearLayoutManager mLinearlayoutManager = new LinearLayoutManager(InvitaBabyShower.this);
            //GridLayoutManager mGridLayoutManager = new GridLayoutManager(this,2);
            recyclerInvitaBShower.setAdapter(mInvitacionAdapter);
            recyclerInvitaBShower.setLayoutManager(mLinearlayoutManager);
            recyclerInvitaBShower.setHasFixedSize(true);
            //recyclerInvitaBShower.setLayoutManager(mGridLayoutManager);
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
        //barTextCount.setText(itemCount);
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
                startActivity(new Intent(context, ShopCartActivity.class)
                        .putExtra("email",email)
                        .putExtra("psw",psw)
                        .putExtra("nombre",username)
                        .putExtra("resSeguridad",resSeguridad)
                        .putExtra("pregSeguridad",pregSeguridad)
                        .putExtra("fromInicio", false)
                        .putExtra("activityFrom",InvitaBabyShower.class)
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
        //return super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this, TipoInvitaActivity.class)
                        .putExtra("email", email)
                        .putExtra("psw", psw)
                        .putExtra("nombre",username)
                        .putExtra("img_str",img_str)
                );
                break;
                case R.id.cart_toolbar:
                    //Launch shop cart Activity
                    break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //uid = mAuth.getUid();
    }

    public interface MyCallback{
        void onGetSelectedList(List<ShopItem> list);
        void onGetSizeList(int selectedSize);
        void onGetSelectedInvitas(List<Invitacion> list);
    }
}
