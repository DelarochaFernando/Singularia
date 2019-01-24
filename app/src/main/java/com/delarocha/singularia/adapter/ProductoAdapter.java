package com.delarocha.singularia.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Invitacion;
import com.delarocha.singularia.auxclasses.ShopItem;
import com.delarocha.singularia.auxclasses.Tools;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmata on 31/12/2018.
 */

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoVH> {

    Context context;
    String mTipoInvitacion;
    List<String> mListaModelos;
    List<Invitacion> mListaInvitaciones;
    List<ShopItem> mShopItemList = new ArrayList<>();
    Invitacion mInvitacion;
    FirebaseFirestore mFiresStoreDB;
    DocumentReference docRef;
    String user,email;
    int itemCount;
    Tools mTools;
    ShopItem mShopItem;
    boolean itemSelected = false;
    boolean shopItemFromDB = false;
    AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    ProductoAdapter.CustomItemClickListener customItemClickListener;

    public ProductoAdapter(Context context,String tipoInvitacion,List<String> listaModelos,
                            List<Invitacion> listaInvitaciones,
                            List<ShopItem> shopItemList,
                            ProductoAdapter.CustomItemClickListener customItemClickListener) {
        this.context = context;
        this.mListaInvitaciones = listaInvitaciones;
        this.mTipoInvitacion = tipoInvitacion; //[BabyShower:0, Bautizo:1, Boda:2, Despedida:3, FiestaInf:4, FiestaTem:5, Graduación:6, XV:7]
        this.mListaModelos = listaModelos;
        this.mTools = new Tools(context);
        this.mShopItemList = shopItemList;
        this.mFiresStoreDB = FirebaseFirestore.getInstance();
        this.customItemClickListener = customItemClickListener;
        itemCount = mTools.getIntPreference(Tools.ITEMCOUNT_KEYNAME);
    }

    public ProductoAdapter(Context context,String tipoInvitacion,List<String> listaModelos,
                           List<Invitacion> listaInvitaciones,
                           ProductoAdapter.CustomItemClickListener customItemClickListener) {
        this.context = context;
        this.mListaInvitaciones = listaInvitaciones;
        this.mTipoInvitacion = tipoInvitacion; //[BabyShower:0, Bautizo:1, Boda:2, Despedida:3, FiestaInf:4, FiestaTem:5, Graduación:6, XV:7]
        this.mListaModelos = listaModelos;
        this.mTools = new Tools(context);
        this.mFiresStoreDB = FirebaseFirestore.getInstance();
        this.customItemClickListener = customItemClickListener;
        itemCount = mTools.getIntPreference(Tools.ITEMCOUNT_KEYNAME);

    }

    @Override
    public ProductoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.producto_item,null);
        final ProductoVH vh = new ProductoVH(v);
        String nombreS = "";
        String nombre = "";

        /*if(mShopItemList!=null){
            if(mShopItemList.size()!=0){
                for(ShopItem shopItem: mShopItemList){
                    nombreS = shopItem.getNombrePdto();
                    for(Invitacion invitacion: mListaInvitaciones){
                        nombre = invitacion.getNombreInvita();
                        if(nombre.equals(nombreS)){
                            vh.btnSelect.setVisibility(View.VISIBLE);
                            vh.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button_accent));
                            vh.btnSelect.setTextColor(context.getResources().getColor(R.color.colorBlack));
                            vh.btnSelect.setText("Quitar");
                            vh.spinnerQty.setEnabled(false);
                            vh.txtPrecio.setTextColor(context.getResources().getColor(R.color.grey_500));
                        }
                    }
                }
            }
        }*/


        /*vh.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customItemClickListener.onItemClick(view, vh.getAdapterPosition());
            }
        });*/
        
        
        return vh;
    }

    @Override
    public void onBindViewHolder(final ProductoVH holder, int position) {

        Integer invitaType = Integer.valueOf(mTipoInvitacion);
        switch (invitaType){
            case 0:
                /*if(mShopItemList.size()!=0){
                    //invitaBabySetUpSelected(holder,position,mShopItemList,mListaInvitaciones);
                }else {
                    invitaBabySetUp(holder,position, mListaInvitaciones);
                }*/
                invitaBabySetUp(holder,position, mListaInvitaciones);
                break;
            case 1:
                break;
            case 2:
                /*
                if(mShopItemList.size()!=0){
                    invitaBodaSetUpSelected(holder,position,mShopItemList,mListaInvitaciones);
                }else {
                    invitaBodaSetUp(holder,position,mListaInvitaciones);
                }
                */
                invitaBodaSetUp(holder,position,mListaInvitaciones);
                //invitaBodaSetUpSelected(holder,position,mShopItemList,mListaInvitaciones);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mListaInvitaciones.size();
    }

    public int getShopCartItemQty(){
        return itemCount;
    }

    private void invitaBodaSetUp(final ProductoVH holder, final int position, List<Invitacion> list){

        final Invitacion inv = list.get(position);
        final ShopItem item = new ShopItem();
        ShopItem item3 = new ShopItem();
        int qty = 0;
        boolean onShopCar = false;

        holder.txtItemName.setText(inv.getNombreInvita());
        item.setNombrePdto(inv.getNombreInvita());
        Picasso.with(context).load(inv.getImg_string()).fit().into(holder.imgItem);
        ArrayAdapter<String>
                spinnerAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item,
                context.getResources().getStringArray(R.array.cantidad_invitaciones_boda));
        holder.spinnerQty.setAdapter(spinnerAdapter);
        mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                int cant = Integer.valueOf((String)parent.getItemAtPosition(pos));
                item.setCantidadPdto(String.valueOf(cant));
                switch (cant){
                    case 0:
                        holder.txtPrecio.setText("$0.00");
                        holder.btnSelect.setVisibility(View.GONE);
                        break;
                    case  250:
                        //item.setCantidadPdto("250");
                        if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$800.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }else {holder.txtPrecio.setText("$1000.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }
                        holder.btnSelect.setVisibility(View.VISIBLE);
                        break;
                    case 350:
                        //item.setCantidadPdto("350");
                        if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$950.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }else {holder.txtPrecio.setText("$1500.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }
                        holder.btnSelect.setVisibility(View.VISIBLE);
                        break;
                    case 450:
                        //item.setCantidadPdto("450");
                        if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$1400.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }else {holder.txtPrecio.setText("$2000.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }
                        holder.btnSelect.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                holder.txtPrecio.setText("$0.00");
                holder.btnSelect.setVisibility(View.GONE);
            }
        };

        if(inv.isOnShopCar()){

            for(ShopItem shopItem: mShopItemList){
                if(inv.getNombreInvita().equals(shopItem.getNombrePdto())){
                    holder.btnSelect.setVisibility(View.VISIBLE);
                    holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button_accent));
                    holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorBlack));
                    holder.btnSelect.setText("Quitar");

                    holder.spinnerQty.setEnabled(false);
                    holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.grey_500));
                    qty = Integer.valueOf(shopItem.getCantidadPdto());
                    switch (qty){
                        case 250:
                            holder.spinnerQty.setSelection(1);
                            holder.btnSelect.setVisibility(View.VISIBLE);
                            holder.txtPrecio.setText(shopItem.getPrecioPdto());
                            break;
                        case 350:
                            holder.spinnerQty.setSelection(2);
                            holder.btnSelect.setVisibility(View.VISIBLE);
                            holder.txtPrecio.setText(shopItem.getPrecioPdto());
                            break;
                        case 450:
                            holder.spinnerQty.setSelection(3);
                            holder.btnSelect.setVisibility(View.VISIBLE);
                            holder.txtPrecio.setText(shopItem.getPrecioPdto());
                            break;
                    }

                }
            }
            String email = mTools.getStringPreference("email");
            String user = mTools.getStringPreference("user");
            //item.setNombrePdto(inv.getNombreInvita());
            item.setImgPdto(inv.getImg_string());
            item.setCategoriaPdto("Invitaciones");
            item.setComentario("");
            item.setModeloPdto(inv.getModeloInvita());
            item.setTipoPdto(inv.getTipoInvita());
            item.setTieneOferta(inv.isTieneOferta());
            item.setNombreUsuario(user);
            item.setEmailUsuario(email);

            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date currentDate;
                    if(itemSelected){
                        itemSelected = false;
                        itemCount--;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount--);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        holder.btnSelect.setText("Agregar");
                        holder.spinnerQty.setEnabled(true);
                        holder.txtPrecio.setEnabled(true);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(false);
                        inv.setOnShopCar(false);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }else {
                        itemSelected = true;
                        itemCount++;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount++);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button_accent));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        holder.btnSelect.setText("Quitar");
                        holder.spinnerQty.setEnabled(false);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.grey_500));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(true);
                        inv.setOnShopCar(true);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }
                }
            });

        }else{
            //invitaciones sin cambios.
            holder.spinnerQty.setOnItemSelectedListener(mOnItemSelectedListener);
            String email = mTools.getStringPreference("email");
            String user = mTools.getStringPreference("user");
            //item.setNombrePdto(inv.getNombreInvita());
            item.setImgPdto(inv.getImg_string());
            item.setCategoriaPdto("Invitaciones");
            item.setComentario("");
            item.setModeloPdto(inv.getModeloInvita());
            item.setTipoPdto(inv.getTipoInvita());
            item.setTieneOferta(inv.isTieneOferta());
            item.setNombreUsuario(user);
            item.setEmailUsuario(email);

            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date currentDate;
                    if(itemSelected){
                        itemSelected = false;
                        itemCount--;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount--);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        holder.btnSelect.setText("Agregar");
                        holder.spinnerQty.setEnabled(true);
                        holder.txtPrecio.setEnabled(true);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(false);
                        inv.setOnShopCar(false);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }else {
                        itemSelected = true;
                        itemCount++;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount++);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button_accent));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        holder.btnSelect.setText("Quitar");
                        holder.spinnerQty.setEnabled(false);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.grey_500));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(true);
                        inv.setOnShopCar(true);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }
                }
            });
        }
    }

    private void invitaBabySetUp(final ProductoVH holder, final int position, List<Invitacion> list){

        final Invitacion inv = list.get(position);
        final ShopItem item = new ShopItem();
        ShopItem item3 = new ShopItem();
        int qty = 0;
        boolean onShopCar = false;

        holder.txtItemName.setText(inv.getNombreInvita());
        item.setNombrePdto(inv.getNombreInvita());
        Picasso.with(context).load(inv.getImg_string()).fit().into(holder.imgItem);
        ArrayAdapter<String>
                spinnerAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item,
                context.getResources().getStringArray(R.array.cantidad_invitaciones_boda));
        holder.spinnerQty.setAdapter(spinnerAdapter);
        mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                int cant = Integer.valueOf((String)parent.getItemAtPosition(pos));
                item.setCantidadPdto(String.valueOf(cant));
                switch (cant){
                    case 0:
                        holder.txtPrecio.setText("$0.00");
                        holder.btnSelect.setVisibility(View.GONE);
                        break;
                    case  250:
                        //item.setCantidadPdto("250");
                        if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$800.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }else {holder.txtPrecio.setText("$1000.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }
                        holder.btnSelect.setVisibility(View.VISIBLE);
                        break;
                    case 350:
                        //item.setCantidadPdto("350");
                        if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$950.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }else {holder.txtPrecio.setText("$1500.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }
                        holder.btnSelect.setVisibility(View.VISIBLE);
                        break;
                    case 450:
                        //item.setCantidadPdto("450");
                        if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$1400.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }else {holder.txtPrecio.setText("$2000.00");
                            item.setPrecioPdto(holder.txtPrecio.getText().toString());
                        }
                        holder.btnSelect.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                holder.txtPrecio.setText("$0.00");
                holder.btnSelect.setVisibility(View.GONE);
            }
        };

        if(inv.isOnShopCar()){

            for(ShopItem shopItem: mShopItemList){
                if(inv.getNombreInvita().equals(shopItem.getNombrePdto())){
                    holder.btnSelect.setVisibility(View.VISIBLE);
                    holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button_accent));
                    holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorBlack));
                    holder.btnSelect.setText("Quitar");

                    holder.spinnerQty.setEnabled(false);
                    holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.grey_500));
                    qty = Integer.valueOf(shopItem.getCantidadPdto());
                    switch (qty){
                        case 250:
                            holder.spinnerQty.setSelection(1);
                            holder.btnSelect.setVisibility(View.VISIBLE);
                            holder.txtPrecio.setText(shopItem.getPrecioPdto());
                            break;
                        case 350:
                            holder.spinnerQty.setSelection(2);
                            holder.btnSelect.setVisibility(View.VISIBLE);
                            holder.txtPrecio.setText(shopItem.getPrecioPdto());
                            break;
                        case 450:
                            holder.spinnerQty.setSelection(3);
                            holder.btnSelect.setVisibility(View.VISIBLE);
                            holder.txtPrecio.setText(shopItem.getPrecioPdto());
                            break;
                    }

                }
            }
            String email = mTools.getStringPreference("email");
            String user = mTools.getStringPreference("user");
            //item.setNombrePdto(inv.getNombreInvita());
            item.setImgPdto(inv.getImg_string());
            item.setCategoriaPdto("Invitaciones");
            item.setComentario("");
            item.setModeloPdto(inv.getModeloInvita());
            item.setTipoPdto(inv.getTipoInvita());
            item.setTieneOferta(inv.isTieneOferta());
            item.setNombreUsuario(user);
            item.setEmailUsuario(email);

            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date currentDate;
                    if(itemSelected){
                        itemSelected = false;
                        itemCount--;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount--);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        holder.btnSelect.setText("Agregar");
                        holder.spinnerQty.setEnabled(true);
                        holder.txtPrecio.setEnabled(true);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(false);
                        inv.setOnShopCar(false);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }else {
                        itemSelected = true;
                        itemCount++;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount++);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button_accent));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        holder.btnSelect.setText("Quitar");
                        holder.spinnerQty.setEnabled(false);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.grey_500));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(true);
                        inv.setOnShopCar(true);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }
                }
            });

        }else{
            //invitaciones sin cambios.
            holder.spinnerQty.setOnItemSelectedListener(mOnItemSelectedListener);
            String email = mTools.getStringPreference("email");
            String user = mTools.getStringPreference("user");
            //item.setNombrePdto(inv.getNombreInvita());
            item.setImgPdto(inv.getImg_string());
            item.setCategoriaPdto("Invitaciones");
            item.setComentario("");
            item.setModeloPdto(inv.getModeloInvita());
            item.setTipoPdto(inv.getTipoInvita());
            item.setTieneOferta(inv.isTieneOferta());
            item.setNombreUsuario(user);
            item.setEmailUsuario(email);

            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date currentDate;
                    if(itemSelected){
                        itemSelected = false;
                        itemCount--;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount--);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorWhite));
                        holder.btnSelect.setText("Agregar");
                        holder.spinnerQty.setEnabled(true);
                        holder.txtPrecio.setEnabled(true);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(false);
                        inv.setOnShopCar(false);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }else {
                        itemSelected = true;
                        itemCount++;
                        mTools.setIntPreference(Tools.ITEMCOUNT_KEYNAME,itemCount++);
                        holder.btnSelect.setBackground(context.getDrawable(R.drawable.roundedshape_button_accent));
                        //holder.btnSelect.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                        holder.btnSelect.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        holder.btnSelect.setText("Quitar");
                        holder.spinnerQty.setEnabled(false);
                        holder.txtPrecio.setTextColor(context.getResources().getColor(R.color.grey_500));
                        currentDate = Calendar.getInstance().getTime();
                        item.setFecha(currentDate.toString());
                        item.setOnShopCar(true);
                        inv.setOnShopCar(true);
                        updateInvitaOnShopCart(inv);
                        updateItemOnShopCart(itemSelected, item);
                    }
                }
            });
        }
    }

    public void updateItemOnShopCart(boolean itemSelected, ShopItem ShopItem){

        email = mTools.getStringPreference("email");
        user = mTools.getStringPreference("user");
        String fecha = ShopItem.getFecha();
        if(itemSelected){
            try{
                //Add Item to ShopCart
                Map<String, Object> shopItem = new HashMap<>();
                shopItem.put("nombreUsuario",ShopItem.getNombreUsuario());
                shopItem.put("emailUsuario",ShopItem.getEmailUsuario());
                shopItem.put("fecha",fecha);
                shopItem.put("nombrePdto",ShopItem.getNombrePdto());
                shopItem.put("imgPdto",ShopItem.getImgPdto());
                shopItem.put("categoriaPdto",ShopItem.getCategoriaPdto());
                shopItem.put("tipoPdto",ShopItem.getTipoPdto());
                shopItem.put("modeloPdto",ShopItem.getModeloPdto());
                shopItem.put("cantidadPdto",ShopItem.getCantidadPdto());
                shopItem.put("comentario",ShopItem.getComentario());
                shopItem.put("precioPdto",ShopItem.getPrecioPdto());
                shopItem.put("tieneOferta",ShopItem.getTieneOferta());
                shopItem.put("onShopCar",ShopItem.isOnShopCar());
                mFiresStoreDB.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
                        .document(user+"-"+email+"-"+fecha)
                        .set(shopItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("SHOPITEM","ITEM Added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("SHOPITEM","ITEM save failed");
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }    
        }else{
            //Delete Item from ShopCart.
            mFiresStoreDB.collection(Tools.FIRESTORE_SHOPSESSION_COLLECTION)
                    .document(user+"-"+email+"-"+fecha)
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("SHOPITEM","ITEM Deleted successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("SHOPITEM","ITEM Deleted failed");
                }
            });
        }
    }

    public void updateInvitaOnShopCart(final Invitacion invitacion){

        try{

            String nombreLower = invitacion.getNombreInvita().toLowerCase();
            if(nombreLower.contains("dorado")){
                nombreLower = "bodadorado";
            }else if(nombreLower.contains("blanco")){
                nombreLower = "bodablanco";
            }else if(nombreLower.contains("plateado")){
                nombreLower = "bodaplateado";
            }else if(nombreLower.contains("negro")){
                nombreLower = "bodanegro";
            }else{
                nombreLower = nombreLower.replace(" ","");
            }
            String estilo = "estilo"+ invitacion.getModeloInvita();
            Log.i("INVITANAME", "invita-"+nombreLower+"-"+estilo);

            DocumentReference InvitaRef = mFiresStoreDB.collection(Tools.FIRESTORE_INVITACIONES_COLLECTION)
                    .document("invita-"+nombreLower+"-"+estilo);
            InvitaRef.update("onShopCar", invitacion.isOnShopCar())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("INVITABODA "+invitacion.getNombreInvita(), "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("INVITABODA "+invitacion.getNombreInvita(), "Error updating document", e);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    public void selected(List<Invitacion> mInvitasSelectedList) {
        mSelectedInvitas = new ArrayList<>();
        mSelectedInvitas = mInvitasSelectedList;
    }
    */


    public class ProductoVH extends RecyclerView.ViewHolder {

        ImageView imgItem;
        CardView cardOferta,cardContainer;
        TextView txtItemName, txtPrecio;
        Spinner spinnerQty;
        Button btnSelect;

        public ProductoVH(View itemView) {
            super(itemView);

            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            cardOferta = (CardView)itemView.findViewById(R.id.cardOferta);
            cardContainer = (CardView) itemView.findViewById(R.id.cardContainer);
            txtItemName = (TextView)itemView.findViewById(R.id.txtItemName);
            txtPrecio = (TextView)itemView.findViewById(R.id.txtPrecio);
            spinnerQty = (Spinner)itemView.findViewById(R.id.spinnerQty);
            btnSelect = (Button)itemView.findViewById(R.id.btnSelect);
        }

    }

    public interface CustomItemClickListener {

        public int onItemClick(View view, int position);
    }
}
