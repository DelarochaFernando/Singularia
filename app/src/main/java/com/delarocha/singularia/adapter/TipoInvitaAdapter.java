package com.delarocha.singularia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.delarocha.singularia.R;
import com.delarocha.singularia.activities.invita.InvitaBabyShower;
import com.delarocha.singularia.activities.invita.InvitaBautizo;
import com.delarocha.singularia.activities.invita.InvitaBoda;
import com.delarocha.singularia.activities.invita.InvitaDespedida;
import com.delarocha.singularia.activities.invita.InvitaFiestaInf;
import com.delarocha.singularia.activities.invita.InvitaFiestaTem;
import com.delarocha.singularia.activities.invita.InvitaGraduacion;
import com.delarocha.singularia.activities.invita.InvitaXVAnos;
import com.delarocha.singularia.auxclasses.TipoInvitaCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class TipoInvitaAdapter extends RecyclerView.Adapter<TipoInvitaAdapter.TipoInvitaVH> {

        private Context ctx;
        private ArrayList<TipoInvitaCard> ArrayListInvitaciones;
        private TipoInvitaCard invitaCard;
        private Class[] classArray;
        private Bundle extras;
        //private Picasso picasso;

    public TipoInvitaAdapter(Context context, ArrayList<TipoInvitaCard> arrayList, Bundle extras){
        this.ctx = context;
        this.ArrayListInvitaciones = arrayList;
        this.invitaCard = new TipoInvitaCard();
        this.classArray = new Class[arrayList.size()];
        this.extras = extras;
        prepareClassArray();
    }

    @Override
    public TipoInvitaVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.invitaciones_item_layout,parent,false);
        TipoInvitaVH vh = new TipoInvitaVH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(TipoInvitaVH holder, final int position) {
        invitaCard = ArrayListInvitaciones.get(position);
        //Picasso.with(ctx).load(invitaCard.getUrl()).fit().into(holder.imgVCard);
        Glide.with(ctx).load(invitaCard.getUrl()).apply(new RequestOptions().centerCrop()).into(holder.imgVCard);
        //Picasso.with(ctx).load(invitaCard.getUrl()).resize(48,48)
                //.into(holder.imgVCard);
        //holder.imgVCard.setBackgroundResource(invitaCard.getImageSource());
        String titulo = invitaCard.getTitulo();

        if(titulo.equals("Despedida de Soltero(a)")){
            holder.titleCard.setTextSize(14);
            holder.titleCard.setText(titulo);
        }else{
            holder.titleCard.setText(invitaCard.getTitulo());
        }

        try{

            holder.imgVCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ctx.startActivity(new Intent(ctx,classArray[position])
                    .putExtra("email",extras.getString("email"))
                    .putExtra("psw",extras.getString("psw"))
                    .putExtra("nombre", extras.getString("nombre"))
                    //.putExtra("img_str", extras.getString("img_str"))
                    );
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return ArrayListInvitaciones.size();
    }

    public class TipoInvitaVH extends RecyclerView.ViewHolder{

        public TextView titleCard;
        public ImageView imgVCard;

        public TipoInvitaVH(View v){
            super(v);
            titleCard = (TextView) v.findViewById(R.id.titleCard);
            imgVCard = (ImageView)v.findViewById(R.id.imgVCard);
        }
    }

    public void prepareClassArray(){
        classArray[0] = InvitaBoda.class;
        classArray[1] = InvitaBabyShower.class;
        classArray[2] = InvitaBautizo.class;
        classArray[3] = InvitaFiestaInf.class;
        classArray[4] = InvitaXVAnos.class;
        classArray[5] = InvitaGraduacion.class;
        classArray[6] = InvitaFiestaTem.class;
        classArray[7] = InvitaDespedida.class;
    }
}
