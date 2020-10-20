package com.parkingapp.homeactivity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import Notifica.Notifica;

public class MostraParcheggio extends FragmentActivity implements OnMapReadyCallback {


    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;

    TextView tvParcheggio=null;
    Button bttElimina=null;
    Button bttTornaAllaHome=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostra_parcheggio);

        Intent i= getIntent();

        tvParcheggio=findViewById(R.id.tvParcheggioMostraParcheggio);
        bttElimina=findViewById(R.id.bttEliminaParcheggio_MostraParcheggio);
        bttTornaAllaHome=findViewById(R.id.btTornaAHome);

        //Per farvedere solo la parte con il parcheggio, ingrandisce in automatico su quella parte
       fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
       fetchLastLocation();

       String via= i.getStringExtra("Via");

        //Creo la notifica per avvisare l'utente che il parcheggio è stato registrato
        //Se via==null, allora questa activity l'ho raggiunta dal Fragment parcheggio e non devo mandare notifiche
       if(via!=null) {
           Notifica notifica = new Notifica();
           notifica.createNotificationChannel(this, NotificationManager.IMPORTANCE_DEFAULT);
           notifica.creaNotifica(this, via, "Parcheggio registrato");
       }

       SharedPreferences sharedPreferences = getSharedPreferences("PARCHEGGIO", Context.MODE_PRIVATE);

       tvParcheggio.setText(sharedPreferences.getString("PARCHEGGIO", ""));

       bttTornaAllaHome.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i= new Intent(getString(R.string.MAIN_TO_HOME));
               startActivity(i);
           }
       });

       bttElimina.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i= new Intent (getString(R.string.ELIMINA_PARCHEGGIO));
               startActivity(i);
           }
       });

    }

    //Verifico di avere i permessi
    private void fetchLastLocation()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.google_map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(MostraParcheggio.this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //Recupero le coordinate salvate
        SharedPreferences sharedPreferences= getSharedPreferences("COORDINATE", Context.MODE_PRIVATE);
        String latitudine=sharedPreferences.getString("LATITUDINE", "");
        String longitudine=sharedPreferences.getString("LONGITUDINE", "");

        double[] coordinate = {Double.parseDouble(latitudine), Double.parseDouble(longitudine)};

        LatLng latLng = new LatLng(coordinate[0], coordinate[1]);
        MarkerOptions markerOptions= new MarkerOptions().position(latLng).title("Parcheggio");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));//Per poterci muovere sulla mappa
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); //Per impostare lo zzom iniziale della mapppa
        googleMap.addMarker(markerOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    fetchLastLocation();
                }
                break;
        }
    }
}
