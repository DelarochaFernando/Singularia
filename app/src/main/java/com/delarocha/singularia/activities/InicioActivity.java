package com.delarocha.singularia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.delarocha.singularia.R;
import com.delarocha.singularia.auxclasses.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class InicioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String FIRESTORE_ACCOUNTS_COLLECTION = "accounts";
    private Context context = this;
    private String email, psw;
    private FirebaseFirestore DBfirebase;
    private DocumentReference dRef;
    private Account account;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private CircleImageView imageUser;
    private TextView textUserName, textUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Singularia");

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        psw = extras.getString("psw");
        DBfirebase = FirebaseFirestore.getInstance();
        account = new Account();


        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getUsuario2(email,psw);
        //textUserName.setText(account.getNombre()+" "+ account.getLastname());
        //textUserEmail.setText(account.getEmail());
        /*try{
            byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
            Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageUser.setImageBitmap(userBitmap);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        //pageIndicatorView
    }

    private void getUsuario2(String email, String psw){
        try{
            dRef = DBfirebase.collection(FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        //Account acc = new Account();
                        DocumentSnapshot snapshot = task.getResult();
                        account.setNombre(snapshot.get("nombre").toString());
                        account.setEmail(snapshot.get("email").toString());
                        account.setImg_string(snapshot.get("img_string").toString());
                        textUserName = (TextView)findViewById(R.id.textUserName);
                        imageUser = (CircleImageView)findViewById(R.id.imageUser);
                        textUserEmail = (TextView) findViewById(R.id.textUserEmail);
                        textUserName.setText(account.getNombre());
                        textUserEmail.setText(account.getEmail());
                        byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                        Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageUser.setImageBitmap(userBitmap);
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
    private void getUsuario(String email, String psw) {
        try{
            dRef = DBfirebase.collection(FIRESTORE_ACCOUNTS_COLLECTION).document(email+"-"+psw);
            dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //user[0] = new Usuario();
                    //user[0].setNombre(documentSnapshot.getString("nombre"));
                    //user[0].setPassword(documentSnapshot.getString("password"));
                    if(documentSnapshot.exists()){
                        account = documentSnapshot.toObject(Account.class);
                        //textUserName.setText(account.getNombre()+" "+ account.getLastname());
                        textUserEmail.setText(account.getEmail());
                        try{
                            byte[] decodedString = Base64.decode(account.getImg_string(), Base64.DEFAULT);
                            Bitmap userBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageUser.setImageBitmap(userBitmap);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        /*usuario.setNombre(documentSnapshot.getString("nombre"));
                        usuario.setLastname(documentSnapshot.getString("lastname"));
                        usuario.setSurname(documentSnapshot.getString("surname"));
                        usuario.setPassword(documentSnapshot.getString("password"));
                        usuario.setEmail(documentSnapshot.getString("email"));
                        usuario.setImg_string(documentSnapshot.getString("img_string"));*/
                    }else{

                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_invitaciones:
                startActivity(new Intent(InicioActivity.this,TipoInvitaActivity.class)
                .putExtra("email", email)
                .putExtra("psw", psw)
                );
                break;
            case R.id.nav_pub_escrita:
                Intent intent = new Intent(InicioActivity.this, PubEscritaActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_playeras:
                break;
            case R.id.nav_configuracion:
                break;

            case R.id.nav_share:
                break;

            case R.id.nav_send:
                break;
            case R.id.nav_exit:
                showClosingDialog();
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(context, LoginActivity.class));
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
}
