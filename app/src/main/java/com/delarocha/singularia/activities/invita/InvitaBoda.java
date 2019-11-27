package com.delarocha.singularia.activities.invita;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.activities.InicioActivity;
import com.delarocha.singularia.activities.ShopCartActivity;
import com.delarocha.singularia.activities.TipoInvitaActivity;
import com.delarocha.singularia.adapter.InvitacionAdapter;
import com.delarocha.singularia.adapter.ProductoAdapter;
import com.delarocha.singularia.auxclasses.Invitacion;
import com.delarocha.singularia.auxclasses.ShopItem;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;



public class InvitaBoda extends AppCompatActivity {

    private String email, psw,username,img_str, itemCount, uid, resSeguridad, pregSeguridad;
    private RecyclerView recyclerInvitaBoda;
    private InvitacionAdapter mInvitacionAdapter;
    private TextView barTextCount;
    private int mItemSelected;
    private Context context = this;

    private DocumentReference docRef;
    private CollectionReference collRef;
    private FirebaseFirestore mFireStoreDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private List<Invitacion> mListaInvitaciones, mInvitasSelectedList;
    private List<ShopItem> shopItemsList;
    private List<String> mListaModelos;
    public List<Map<String, Object>> mapList = new ArrayList<>();
    public List<Map<String, Object>> mapListSelected = new ArrayList<>();
    private Invitacion invitacion;
    private Tools mTools;
    private boolean fromInicio = false;
    ProductoAdapter.CustomItemClickListener itemCountCallBack;
    InvitaBoda.myCallback myCallback;


    //int selected = 0;
    String TAG = "INVITABODA";
    private ImageView imgIconShopCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invita_boda);
        getSupportActionBar().setTitle("Boda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTools = new Tools(this);
        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        resSeguridad = extras.getString("resSeguridad");
        pregSeguridad = extras.getString("pregSeguridad");
        psw = extras.getString("psw");
        username = extras.getString("nombre");
        img_str = extras.getString("img_str");

        recyclerInvitaBoda = (RecyclerView) findViewById(R.id.recyclerInvitaBoda);
        mFireStoreDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        getSelectedItems(new myCallback() {
            @Override
            public void onGetSizeList(int selectedSize) {
                mItemSelected = selectedSize;
            }
            @Override
            public void onGetSelectedList(List<ShopItem> list) {
                shopItemsList = list;
            }
            @Override
            public void onGetSelectedInvitas(List<Invitacion> list) {mInvitasSelectedList = list;}
        });
        //selected = getSelectedItems();
        getInvitaciones();

    }

    private void getSelectedItems(final InvitaBoda.myCallback callback) {

        final List<Invitacion> SelectedList = new ArrayList<>();
        final List<ShopItem> ShopItemList = new ArrayList<>();

        //try{
            mFireStoreDB.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            //uid = mAuth.getUid();
                            if(document.getId().contains(uid)){
                                mapListSelected.add(document.getData());
                            }
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
                                            item.getNombrePdto(),
                                            item.getTipoPdto(),
                                            item.getModeloPdto(),
                                            item.getImgPdto(),
                                            item.getCantidadPdto(),
                                            item.getPrecioPdto(),
                                            item.getComentario(),
                                            oferta,onShopCar
                                    );

                            if(item.getTipoPdto().equals("2")){//ShopItem Invitacion Boda
                                ShopItemList.add(item);
                            }
                            if(invitacion.getTipoInvita().equals("2")){//Invitacion Boda
                                SelectedList.add(invitacion);
                            }
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
        //}catch (Exception e){
            //e.printStackTrace();
        //}
    }

    public void getInvitaciones(){
        try{
            mFireStoreDB.collection(Tools.FIRESTORE_INVITACIONES_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    mapList.add(document.getData());
                                }
                                //getSelectedItems();
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
            mListaModelos = new ArrayList<>();
            List<List<Invitacion>> data = new ArrayList<>();
            List<Invitacion> clasico = new ArrayList<>();
            List<Invitacion> mapa = new ArrayList<>();
            boolean oferta = false;
            boolean onShopCar = false;
            //data.add(mListaInvitaciones);
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
                if(invitacion.getTipoInvita().equals("2")&&invitacion.getModeloInvita().equals("1")){//Invitaciones de BabyShower
                    clasico.add(invitacion);
                }
                if(invitacion.getTipoInvita().equals("2")&&invitacion.getModeloInvita().equals("2")){//Invitaciones de BabyShower
                    mapa.add(invitacion);
                }
            }

            if (clasico.size() != 0) { data.add(clasico); mListaModelos.add("Clásico");}
            if (mapa.size() != 0) { data.add(mapa);mListaModelos.add("Mapa");}

            if(mItemSelected!=0){
                mInvitacionAdapter = new InvitacionAdapter(this,"2",mListaModelos,data,shopItemsList,itemCountCallBack);
                //mInvitacionAdapter = new InvitacionAdapter(this,"2",modelos,data,shopItemsList,itemCountCallBack);

            }else {
                mInvitacionAdapter = new InvitacionAdapter(this,"2",mListaModelos,data,itemCountCallBack);
                //mInvitacionAdapter = new InvitacionAdapter(this,"2",modelos,data,itemCountCallBack);
            }

            //mInvitacionAdapter.selectedItems(mInvitasSelectedList);
            //mInvitacionAdapter = new InvitacionAdapter(this,"2",mListaModelos,data,shopItemsList,itemCountCallBack);
           // mInvitacionAdapter = new InvitacionAdapter(this,"2",mListaModelos,data,itemCountCallBack);
            LinearLayoutManager mLinearlayoutManager = new LinearLayoutManager(InvitaBoda.this);
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(this,2);
            recyclerInvitaBoda.setAdapter(mInvitacionAdapter);
            recyclerInvitaBoda.setLayoutManager(mLinearlayoutManager);
            recyclerInvitaBoda.setHasFixedSize(true);
            //recyclerInvitaBoda.setLayoutManager(mGridLayoutManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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
                        .putExtra("fromInicio",fromInicio)
                        .putExtra("pregSeguridad",pregSeguridad)
                        .putExtra("fromInicio", false)
                        .putExtra("activityFrom",InvitaBoda.class)
                );
            }
        });
        barTextCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopCartDialog();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void showShopCartDialog(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        //View view = getLayoutInflater().inflate(R.layout.shop,null);
    }

    private void getSelectedInvitas(InvitaBoda.myCallback myCallback){}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(InvitaBoda.this, TipoInvitaActivity.class)
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

    public interface myCallback{
        //int onCallBack(int selectedSize);
        //List<Invitacion> onGetSelectedList(List<Invitacion> list);
        void onGetSelectedList(List<ShopItem> list);
        void onGetSizeList(int selectedSize);
        void onGetSelectedInvitas(List<Invitacion> list);
    }
}
