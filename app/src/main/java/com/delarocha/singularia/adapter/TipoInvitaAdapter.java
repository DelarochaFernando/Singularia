package com.delarocha.singularia.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;

public class TipoInvitaAdapter extends RecyclerView.Adapter<TipoInvitaAdapter.TipoInvitaVH> {

        private Context ctx;
        private ArrayList<TipoInvitaCard> ArrayListInvitaciones;
        private TipoInvitaCard invitaCard;
        private Class[] classArray;

    public TipoInvitaAdapter(Context context, ArrayList<TipoInvitaCard> arrayList){
        this.ctx = context;
        this.ArrayListInvitaciones = arrayList;
        this.invitaCard = new TipoInvitaCard();
        this.classArray = new Class[arrayList.size()];
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
        holder.imgVCard.setBackgroundResource(invitaCard.getImageSource());
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
                ctx.startActivity(new Intent(ctx,classArray[position]));
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
