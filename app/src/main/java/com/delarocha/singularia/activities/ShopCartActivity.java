package com.delarocha.singularia.activities;

//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopCartActivity extends AppCompatActivity {
//public class ShopCartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ShopCartActivity";
    private RecyclerView recyclerShopCartItems;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView imageView;
    private MaterialButton btnItem,btnContinuarCompra;
    private DocumentReference dRef;
    private TextView txtProdsLabel,txtSubtotal,txtPriceEnvio,txtPriceTotal;
    private FirebaseFirestore mFirestoreDB;

    private ShopCartAdapter shopCartAdapter;
    private NavigationView navigationView;
    private DrawerLayout drawerShopCart;
    private CircleImageView image_User;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String email, psw, username, pregSeg,resSeguridad, avatar_img_str, avatar_img_storage_path, uid;
    private String sumaSubTotal = "";
    private int sub_total = 0;
    private List<DocumentSnapshot> docSnapshotListFiltered;
    //private List<>
    private Context context = this;
    private boolean fromInicio;
    private boolean subtotalModificado = false;
    private String[] priceArr;
    private Serializable activityFrom;
    private LinearLayout linearData;
    private RelativeLayout relativeNoItems;
    AdapterView.OnItemSelectedListener mOnItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        psw = extras.getString("psw");
        username = extras.getString("nombre");
        pregSeg = extras.getString("pregSeguridad");
        resSeguridad = extras.getString("resSeguridad");
        fromInicio = extras.getBoolean("fromInicio");
        activityFrom = extras.getSerializable("activityFrom");
        //avatar_img_str = extras.getString("img_str");
        mFirestoreDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        /*getUsuario2(email,psw);
        drawerShopCart = (DrawerLayout)findViewById(R.id.drawer_tipo_invita);
        navigationView = (NavigationView)findViewById(R.id.nav_view_tipoInvita);
        MenuItem nav_invitaciones = navigationView.getMenu().findItem(R.id.nav_invitaciones);
        MenuItem nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_invitaciones.setVisible(false);
        nav_home.setVisible(true);

        View header = navigationView.getHeaderView(0);
        recyclerShopCartItems = findViewById(R.id.recyclerShopCartItems);
        ImageView btnConfiguration = header.findViewById(R.id.btnConfiguration);
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
        });*/
        recyclerShopCartItems = findViewById(R.id.recyclerShopCartItems);
        relativeNoItems = findViewById(R.id.relativeNoItems);
        linearData = findViewById(R.id.linearData);
        txtProdsLabel = findViewById(R.id.txtProdsLabel);
        txtPriceTotal = findViewById(R.id.txtPriceTotal);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        getItemsOnShopCart();
        //shopCartAdapter = new ShopCartAdapter();
        //recyclerShopCartItems.setAdapter(shopCartAdapter);
        //mLayoutManager = new GridLayoutManager(this,2);
        //mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        //recyclerShopCartItems.setLayoutManager(mLayoutManager);
        //recyclerShopCartItems.hasFixedSize();
    }

    public void getItemsOnShopCart(){
        mFirestoreDB.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if(e!=null){
                            return;
                        }
                        if(queryDocumentSnapshots!=null){
                            List<DocumentSnapshot> docSnapshotList = new ArrayList<>();
                            docSnapshotListFiltered = new ArrayList<>();
                            docSnapshotList = queryDocumentSnapshots.getDocuments();
                            for(int i = 0; i<docSnapshotList.size();i++){
                                if(docSnapshotList.get(i).getId().contains(mUser.getUid())){
                                    docSnapshotListFiltered.add(docSnapshotList.get(i));

                                    //barTextCount.setText(String.valueOf(docSnapshotListFiltered.size()));
                                }
                            }
                        }
                        txtProdsLabel.setText("Productos"+"("+docSnapshotListFiltered.size()+")");
                        setUpUI(docSnapshotListFiltered);
                    }
                });
    }

    public void setUpUI(List<DocumentSnapshot> lista){

        if(lista.size()!=0){
            relativeNoItems.setVisibility(View.GONE);
            linearData.setVisibility(View.VISIBLE);
            shopCartAdapter = new ShopCartAdapter(lista);
            recyclerShopCartItems.setAdapter(shopCartAdapter);
            //mLayoutManager = new GridLayoutManager(this,2);
            //mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            recyclerShopCartItems.setLayoutManager(mLayoutManager);
            recyclerShopCartItems.hasFixedSize();
            //txtSubtotal.setText("$"+String.valueOf(shopCartAdapter.getSubtotal()));
            ///int envio = txtPriceEnvio.getText().toString();
            //txtPriceTotal.setText("$"+String.valueOf(Integer.getInteger(txtSubtotal.getText().toString())+Integer.getInteger(txtPriceEnvio.getText().toString())));
            //txtPriceTotal.setText("$"+String.valueOf(shopCartAdapter.getSubtotal()+200));
        }else{
            //No items in ShopCart
            relativeNoItems.setVisibility(View.VISIBLE);
            linearData.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(context, (Class<?>) activityFrom)
                        .putExtra("email",email)
                        .putExtra("psw",psw)
                        .putExtra("nombre",username)
                        .putExtra("resSeguridad",resSeguridad)
                        .putExtra("pregSeguridad",pregSeg)
                        .putExtra("fromInicio", fromInicio)
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ShopCartAdapter extends RecyclerView.Adapter<ShopCartAdapter.ShopCartVH>{

        List<DocumentSnapshot> listShopItems;
        //int subtotal_adapter = 0;

        public ShopCartAdapter(List<DocumentSnapshot> lista){
            this.listShopItems = lista;
            //classifyShopItems();
        }

        /*private void classifyShopItems(){

            for(int i = 0; i<listShopItems.size();i++){
              DocumentSnapshot snapshot = listShopItems.get(i);
              snapshot.
            }
        }*/
        public int getSubtotal(){
            //subtotal_adapter = sub_total;
            return sub_total;
        }

        @NonNull
        @Override
        public ShopCartAdapter.ShopCartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.shopcart_item_layout,parent,false);
            ShopCartVH vh = new ShopCartVH(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ShopCartAdapter.ShopCartVH holder, final int position) {

            final DocumentSnapshot snapshot = listShopItems.get(position);
            int qty = 0;

            String precioQty = snapshot.getString("precioPdto");
            precioQty = precioQty.replace("$","");

            priceArr = new String[listShopItems.size()];
            for(int i = 0; i<listShopItems.size();i++){
                String mprecio = listShopItems.get(i).getString("precioPdto");
                mprecio = mprecio.replace("$","");
                priceArr[i] = mprecio;
            }



            sub_total = sub_total+= Float.valueOf(precioQty);
            txtSubtotal.setText("$"+String.valueOf(sub_total));
            txtPriceTotal.setText("$"+String.valueOf(sub_total+200));
            //sumaSubTotal = sumaSubTotal+=snapshot.getString("precioPdto");
            Glide.with(context).load(snapshot.getString("imgPdto"))
                    .apply(new RequestOptions().centerCrop()).into(holder.imgSCItem);
            holder.txtSCNPrecioItem.setText(snapshot.getString("precioPdto"));
            holder.txtSCNombreItem.setText(snapshot.getString("nombrePdto"));
            holder.btnSCQuitarItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //deleteItemfromShopCart(snapshot.getString("nombrePdto"));
                    deleteItemfromShopCart(snapshot);
                    //updateInvitaOnShopCart(inv);
                    //updateItemOnShopCart(itemSelected, item);
                    //updateItemOnShopCart(item);
                }
            });
            ArrayAdapter<String>
                    spinnerAdapter = new ArrayAdapter<String>(context,
                    R.layout.smallertext_spinner,
                    context.getResources().getStringArray(R.array.cantidad_invitaciones_shopcart));

            holder.spinnerQtySCItem.setAdapter(spinnerAdapter);
            mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

                    subtotalModificado = true;
                    sub_total = 0;
                    int cant = Integer.valueOf((String)parent.getItemAtPosition(pos));
                    holder.txtSCNPrecioItem.setText(String.valueOf(cant));

                    switch (cant){
                        case  250:

                            if(snapshot.getBoolean("tieneOferta")){
                                holder.txtSCNPrecioItem.setText("$800");
                                priceArr[position] = String.valueOf(800);
                                for(int i = 0; i<priceArr.length;i++){
                                    sub_total += Integer.valueOf(priceArr[i]);
                                }
                                txtSubtotal.setText("$"+String.valueOf(sub_total));
                                txtPriceTotal.setText("$"+String.valueOf(sub_total+200));
                            }else {
                                holder.txtSCNPrecioItem.setText("$1000");
                                priceArr[position] = String.valueOf(1000);
                                for(int i = 0; i<priceArr.length;i++){
                                    sub_total += Integer.valueOf(priceArr[i]);
                                }
                                txtSubtotal.setText("$"+String.valueOf(sub_total));
                                txtPriceTotal.setText("$"+String.valueOf(sub_total+200));
                            }

                            break;
                        case 350:

                            if(snapshot.getBoolean("tieneOferta")){holder.txtSCNPrecioItem.setText("$950");
                                priceArr[position] = String.valueOf(950);
                                for(int i = 0; i<priceArr.length;i++){
                                    sub_total += Integer.valueOf(priceArr[i]);
                                }
                                txtSubtotal.setText("$"+String.valueOf(sub_total));
                                txtPriceTotal.setText("$"+String.valueOf(sub_total+200));
                            }else {
                                holder.txtSCNPrecioItem.setText("$1500");
                                priceArr[position] = String.valueOf(1500);
                                for(int i = 0; i<priceArr.length;i++){
                                    sub_total += Integer.valueOf(priceArr[i]);
                                }
                                txtSubtotal.setText("$"+String.valueOf(sub_total));
                                txtPriceTotal.setText("$"+String.valueOf(sub_total+200));

                            }
                            break;
                        case 450:

                            if(snapshot.getBoolean("tieneOferta")){holder.txtSCNPrecioItem.setText("$1400");
                                priceArr[position] = String.valueOf(1400);
                                for(int i = 0; i<priceArr.length;i++){
                                    sub_total += Integer.valueOf(priceArr[i]);
                                }
                                txtSubtotal.setText("$"+String.valueOf(sub_total));
                                txtPriceTotal.setText("$"+String.valueOf(sub_total+200));
                            }else {
                                holder.txtSCNPrecioItem.setText("$2000");
                                priceArr[position] = String.valueOf(2000);
                                for(int i = 0; i<priceArr.length;i++){
                                    sub_total += Integer.valueOf(priceArr[i]);
                                }
                                txtSubtotal.setText("$"+String.valueOf(sub_total));
                                txtPriceTotal.setText("$"+String.valueOf(sub_total+200));
                            }
                            break;
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //holder.txtPrecio.setText("$0.00");
                    //holder.btnSelect.setVisibility(View.INVISIBLE);
                }
            };
            qty = Integer.valueOf(snapshot.getString("cantidadPdto"));
            switch (qty){
                case 250:
                    holder.spinnerQtySCItem.setSelection(0);
                    //holder.btnSelect.setVisibility(View.VISIBLE);
                    holder.txtSCNPrecioItem.setText(snapshot.getString("precioPdto"));
                    break;
                case 350:
                    holder.spinnerQtySCItem.setSelection(1);
                    //holder.btnSelect.setVisibility(View.VISIBLE);
                    holder.txtSCNPrecioItem.setText(snapshot.getString("precioPdto"));
                    break;
                case 450:
                    holder.spinnerQtySCItem.setSelection(2);
                    //holder.btnSelect.setVisibility(View.VISIBLE);
                    holder.txtSCNPrecioItem.setText(snapshot.getString("precioPdto"));
                    break;
            }

            holder.spinnerQtySCItem.setOnItemSelectedListener(mOnItemSelectedListener);
        }

        @Override
        public int getItemCount() {

            return listShopItems.size();
        }

        class ShopCartVH extends RecyclerView.ViewHolder{

            TextView txtSCNombreItem,txtSCNPrecioItem;
            Spinner spinnerQtySCItem;
            ImageView imgSCItem;
            MaterialButton btnSCQuitarItem;

            public ShopCartVH(@NonNull View itemView) {
                super(itemView);
                txtSCNombreItem = itemView.findViewById(R.id.txtSCNombreItem);
                txtSCNPrecioItem = itemView.findViewById(R.id.txtSCNPrecioItem);
                spinnerQtySCItem = itemView.findViewById(R.id.spinnerQtySCItem);
                imgSCItem = itemView.findViewById(R.id.imgSCItem);
                btnSCQuitarItem = itemView.findViewById(R.id.btnSCQuitarItem);
            }
        }
    }

    protected void deleteItemfromShopCart(DocumentSnapshot snap){

        uid = mAuth.getUid();
        String nombrePdto = snap.getString("nombrePdto");
        String precioPdto = snap.getString("precioPdto");
        precioPdto = precioPdto.replace("$","");
        final String finalPrecioPdto = precioPdto;
        mFirestoreDB.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
                .document(uid+"-"+email+"-"+nombrePdto)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //itemSelected = true;
                Log.i("SHOPITEM","ITEM Deleted successfully");
                sub_total = sub_total - Integer.valueOf(finalPrecioPdto);
                shopCartAdapter.notifyDataSetChanged();
                //getItemsOnShopCart();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("SHOPITEM","ITEM Deleted failed");
            }
        });
    }
}
