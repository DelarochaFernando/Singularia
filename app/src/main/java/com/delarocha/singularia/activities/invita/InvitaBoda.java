package com.delarocha.singularia.activities.invita;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.delarocha.singularia.R;
import com.delarocha.singularia.activities.TipoInvitaActivity;

public class InvitaBoda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invita_boda);
        getSupportActionBar().setTitle("Boda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this, TipoInvitaActivity.class));
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}
