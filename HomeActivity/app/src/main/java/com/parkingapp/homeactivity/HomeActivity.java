package com.parkingapp.homeactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import fragment_home_activity.HomeFragment;
import fragment_home_activity.Parcheggio;
import fragment_home_activity.PromemoriaNotifica;
import fragment_home_activity.Utente;

public class HomeActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mToggle=null;
    private DrawerLayout drawerLayout=null;
    TextView tvUsername=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Intent i= getIntent();

        setTitle("Home"); //Esplicito il titolo altrimenti comparirebbe 'HomeActivity'

        drawerLayout =(DrawerLayout) findViewById(R.id.drawer);
        mToggle= new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(mToggle);
        NavigationView navigationView= findViewById(R.id.nvNavigationViewHomeActivity);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);

        //Mi collego alla TextView sull'header
        View headerView = navigationView.getHeaderView(0);
        tvUsername = headerView.findViewById(R.id.tvHeaderHomeActivity);

        //Carico dalla memoria il nome scelto come username
        SharedPreferences sharedPreferences= getSharedPreferences("USERNAME_PASSWORD", MODE_PRIVATE);
        tvUsername.setText(sharedPreferences.getString("USERNAME", "")); //Assegno sotto l'immagine la scritta dell'username


        //Quest parte è identica all parte più sotto e serve a far si che all'apertura si apra la home con i comandi per far partire l'app
        Fragment myFragment=null;
        Class fragmentClass=HomeFragment.class;

        try {
            myFragment=(Fragment) fragmentClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flcontent, myFragment).commit();

    }

    //Associo ad ogni item la classe a cui deve fare riferimento quando premo
    public void selectItemDrawer(MenuItem menuItem)
    {
        Fragment myFragmant=null;
        Class fragmentClass;

        switch (menuItem.getItemId())
        {

            case R.id.btParcheggio:
                fragmentClass= Parcheggio.class;
                break;

            case R.id.btOrario:
                fragmentClass= PromemoriaNotifica.class;
                break;

            case R.id.btUtente:
                fragmentClass= Utente.class;
                break;

            case R.id.btHomeMenu:
                fragmentClass= HomeFragment.class;
                break;

                default:
                    fragmentClass=HomeFragment.class; //Di default torna alla HomeActivity
        }
        try {
            myFragmant=(Fragment) fragmentClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        FragmentManager fragmentManager= getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flcontent, myFragmant).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }

    //Capire qual è l'item selezionato nel menu a tendina
    private void setupDrawerContent (NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }

    //Pulsante per aprire menu a tendina
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return  false;
    }




}
