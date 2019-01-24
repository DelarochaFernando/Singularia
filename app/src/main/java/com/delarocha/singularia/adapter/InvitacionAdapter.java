package com.delarocha.singularia.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Invitacion;
import com.delarocha.singularia.auxclasses.ShopItem;

import java.util.List;

/**
 * Created by jmata on 18/12/2018.
 */

public class InvitacionAdapter extends RecyclerView.Adapter<InvitacionAdapter.ProductoVH> {

    Context context;
    String mTipoInvitacion;
    List<String> mListaModelos;
    List<List<Invitacion>> data;
    List<ShopItem> selected;
    Invitacion mInvitacion;
    ProductoAdapter productoAdapter;
    ProductoAdapter.CustomItemClickListener customItemClickListener;
    AdapterView.OnItemSelectedListener mOnItemSelectedListener;


    /*public InvitacionAdapter(Context context,String tipoInvitacion,List<String> listaModelos, List<Invitacion> listaInvitaciones) {
        this.context = context;
        this.mListaInvitaciones = listaInvitaciones;
        this.mTipoInvitacion = tipoInvitacion; //[BabyShower:0, Bautizo:1, Boda:2, Despedida:3, FiestaInf:4, FiestaTem:5, Graduación:6, XV:7]
        this.mListaModelos = listaModelos;
        this.mInvitacion = new Invitacion();
    }*/
    public InvitacionAdapter(Context context,String tipoInvitacion,List<String> listaModelos,
                             List<List<Invitacion>> listaInvitaciones,
                             ProductoAdapter.CustomItemClickListener itemCountCallBack) {
        this.context = context;
        this.data = listaInvitaciones;
        this.mTipoInvitacion = tipoInvitacion; //[BabyShower:0, Bautizo:1, Boda:2, Despedida:3, FiestaInf:4, FiestaTem:5, Graduación:6, XV:7]
        this.mListaModelos = listaModelos;
        this.selected = null;
        this.mInvitacion = new Invitacion();

    }

    public InvitacionAdapter(Context context,String tipoInvitacion,List<String> listaModelos,
                             List<List<Invitacion>> listaInvitaciones,
                             List<ShopItem> selected,
                             ProductoAdapter.CustomItemClickListener itemCountCallBack) {
        this.context = context;
        this.data = listaInvitaciones;
        this.mTipoInvitacion = tipoInvitacion; //[BabyShower:0, Bautizo:1, Boda:2, Despedida:3, FiestaInf:4, FiestaTem:5, Graduación:6, XV:7]
        this.mListaModelos = listaModelos;
        this.mInvitacion = new Invitacion();
        this.selected = selected;

    }

    @Override
    public ProductoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.title_producto_item,null);
        ProductoVH vh = new ProductoVH(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ProductoVH holder, int position) {


        String modelo = mListaModelos.get(position);
        List<Invitacion> invitas = data.get(position);
        holder.txtModeloInvita.setText(modelo);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context,2);
        if(selected==null){
            productoAdapter = new ProductoAdapter(context,mTipoInvitacion,mListaModelos,invitas,customItemClickListener);
        }else {
            productoAdapter = new ProductoAdapter(context,mTipoInvitacion,mListaModelos,invitas,selected,customItemClickListener);
        }

        holder.recyclerContainer.setAdapter(productoAdapter);
        holder.recyclerContainer.setLayoutManager(mGridLayoutManager);
        holder.recyclerContainer.setHasFixedSize(true);
        /*
        Picasso.with(context).load(mInvitacion.getImg_string()).fit().into(holder.imgItem);

        ArrayAdapter<String>
           spinnerAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item,
                context.getResources().getStringArray(R.array.cantidad_invitaciones_boda));

        holder.spinnerQty.setAdapter(spinnerAdapter);
        mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                int cant = Integer.valueOf((String)parent.getItemAtPosition(pos));
                switch (cant){
                case  250:
                    if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$800.00");}
                    else {holder.txtPrecio.setText("$1000.00");}
                break;
                case 350:
                    if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$950.00");}
                    else {holder.txtPrecio.setText("$1500.00");}
                break;
                case 450:
                    if(holder.cardOferta.isShown()){holder.txtPrecio.setText("$1400.00");}
                    else {holder.txtPrecio.setText("$2000.00");}
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                holder.txtPrecio.setText("$0.00");
            }
        };
        holder.spinnerQty.setOnItemSelectedListener(mOnItemSelectedListener);
        */
    }

    @Override
    public int getItemCount() {
        return mListaModelos.size();
    }

    public int getShopCartItemQty(){
        return productoAdapter.getShopCartItemQty();
    }

    /*
    public void selectedItems(List<Invitacion> mInvitasSelectedList) {
        productoAdapter.selected(mInvitasSelectedList);
    }
    */


    public class ProductoVH extends RecyclerView.ViewHolder{

        TextView txtItemName, txtPrecio, txtModeloInvita;
        RecyclerView recyclerContainer;

        public ProductoVH(View itemView) {
            super(itemView);
            txtModeloInvita = (TextView)itemView.findViewById(R.id.txtModeloInvita);
            recyclerContainer = (RecyclerView)itemView.findViewById(R.id.recyclerContainer);
        }

    }
}
