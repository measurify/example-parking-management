package com.parkingapp.homeactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.androidnetworking.error.ANError;

import Server.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Server.Server;
import Server.WSComunication;
import mist.Variabili;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.MANAGE_OWN_CALLS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    final String TAG="MainActivity";
    Button bttMain=null;
    Context context=this;
    //Variabile solo per un test, da cancellare dopo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttMain=findViewById(R.id.bttMain);

        try {
            Server.Get("/v1/measurements?filter={\"thing\":\"city\"}&limit=10&page=1", new Callback() {
                @Override
                public void onSuccess(JSONObject result) throws JSONException, IOException, InterruptedException {
                    JSONArray docs= result.getJSONArray("docs");
                    String[] impedimenti=new String[docs.length()];
                    String[] data_inizio= new String[docs.length()];
                    String[] data_fine= new String[docs.length()];
                    double[][] coordinate = new double[docs.length()][2];

                    for (int i=0; i<docs.length(); i++) {
                        JSONObject docsJson=docs.getJSONObject(i);
                        JSONArray samples = docsJson.getJSONArray("samples");
                        JSONObject samplesJson = samples.getJSONObject(0);
                        JSONArray values = samplesJson.getJSONArray("values");
                        impedimenti[i] = values.getString(2);

                        JSONObject location=docsJson.getJSONObject("location");
                        JSONArray coordinates = location.getJSONArray("coordinates");
                        coordinate[i][0]= coordinates.getDouble(0);
                        coordinate[i][1]= coordinates.getDouble(1);

                        data_inizio[i]=docsJson.getString("startDate");
                        data_fine[i]=docsJson.getString("endDate");

                    }

                    //IMPLLEMETARE LA NOTIFICA PER GLI IMPEDIMENTI

                }

                @Override
                public void onError(ANError error) throws Exception {

                    Log.e("GET impedimenti", "Errore recupero impedimenti");
                }
            }, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Controllo che i permessi siano abilitati all'avvio dell'app
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE}, 1);


       //al premere del bottone se ricordami è false si passa alla schermata di login
        //altrimenti si va direttamente alla home
       bttMain.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               //Se i permessi non ci sono li richiedo ogni volta che prova a far iniziare l'app
               if(requestPermission(ACCESS_FINE_LOCATION)) {
                   //La scelta per capire se richiedere di identificarsi o no sta sella checkbox, salvata alla voce STATO
                   SharedPreferences sharedPreferences = getSharedPreferences("RICORDAMI", Context.MODE_PRIVATE);
                   boolean stato = sharedPreferences.getBoolean("STATO", false);
                   if (!stato) {
                       Intent i = new Intent(getString(R.string.MAIN_TO_LOGSIGN));
                       startActivity(i);
                   } else {

                       //Aggiorno i  parametri andando a prendeere dal server
                       try {
                           if(Variabili.aggiornaPosizione(context)) {
                               Intent i = new Intent(getString(R.string.MAIN_TO_HOME));
                               WSComunication wsComunication = new WSComunication();
                               wsComunication.startWS(context);
                               startActivity(i);
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               }


           }
       });
    }

    //Chiedo all'utente di attivare tutti i permessi per l'applicazione
    private boolean requestPermission(final String permission)
    {
        final boolean[] permesso = {true};//Valore per capire se il permesso è abilitato, array perchè se no non lo prende dopo

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            permesso[0]=false;

            new AlertDialog.Builder(this)
                    .setTitle("Permessi obbligatori")
                    .setMessage("I permessi sono necessari per il corretto funzionamento dell'applicazione")
                    .setPositiveButton("Concedi permessi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)context, new String[]{ACCESS_FINE_LOCATION}, 1);
                            permesso[0]=true;
                        }
                    })
                    .setNegativeButton("Nega", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            permesso[0] =false;
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
        return permesso[0];
    }

}
