package com.delarocha.singularia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.adapter.TipoInvitaAdapter;
import com.delarocha.singularia.auxclasses.TipoInvitaCard;

import java.util.ArrayList;

public class TipoInvitaActivity extends AppCompatActivity {

    public static String URL_IMAGE_BODA = "www.lomejorenbodas.com/wp-content/uploads/2016/12/Boda-catolica.jpg";
    public static String URL_IMAGE_PROM = "https://www.elsoldepuebla.com.mx/incoming/vema4b-graduacion.jpg/ALTERNATES/LANDSCAPE_1140/graduacion.jpg";

    private RecyclerView recyclerTipoInvita;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView imageView;
    private TextView textView;
    private TipoInvitaAdapter mTipoInvitaAdapter;
    private TipoInvitaCard mTipoInvitaCard;
    private ArrayList<TipoInvitaCard> mTipoInvitaList;

    String email, psw;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipoinvita);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invitaciones");

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        psw = extras.getString("psw");

        prepareCards();
        recyclerTipoInvita = (RecyclerView)findViewById(R.id.recyclerTipoInvita);
        mTipoInvitaAdapter = new TipoInvitaAdapter(this,mTipoInvitaList);
        recyclerTipoInvita.setAdapter(mTipoInvitaAdapter);
        mLayoutManager = new GridLayoutManager(this,2);
        recyclerTipoInvita.setLayoutManager(mLayoutManager);
        recyclerTipoInvita.hasFixedSize();
        //Picasso.with(this).load(URL_IMAGE_PROM).resize(250,250).into(imageView);
        //Picasso.with(this).setLoggingEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this,InicioActivity.class)
                 .putExtra("email", email)
                 .putExtra("psw", psw));
            break;
        }
        return true;
    }

    public void prepareCards(){

        mTipoInvitaList = new ArrayList<TipoInvitaCard>();
        int[] cardImage = new int[]{
        R.drawable.image_boda,
        R.drawable.image_babyshower,
        R.drawable.image_bautizo,
        R.drawable.image_fiesta_infantil,
        R.drawable.image_xv,
        R.drawable.image_graduacion,
        R.drawable.image_fiesta_tematica,
        R.drawable.image_despedida_soltera
        };

        String[] tituloTipoInvita = new String[]{
                "Boda",
                "Baby Shower",
                "Bautizo",
                "Fiestas Infantiles",
                "XV años",
                "Graduacion",
                "Fiesta Temática",
                "Despedida de Soltero(a)"
        };
        for(int i = 0; i < cardImage.length;i++){
            TipoInvitaCard tic = new TipoInvitaCard(tituloTipoInvita[i],cardImage[i]);
            mTipoInvitaList.add(tic);
        }
    }
}