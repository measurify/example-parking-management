package com.parkingapp.homeactivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import Posizione.Posizione;

import Server.CreazioneJson;
import Server.Server;
import asyncTasks.AsyncTaskEsecuzione;
import mist.Variabili;
import Server.Callback;

public class Esecuzione extends AppCompatActivity {

    Button bttAnnulla=null;
    Button bttSalvaParcheggio=null;
    TextView tvErrore=null;
    Context context=this;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esecuzione);

        final Intent i = getIntent();

        bttAnnulla = findViewById(R.id.bttAnnullaEsecuzione);
        bttSalvaParcheggio = findViewById(R.id.bttSalvaParcheggio);
        tvErrore = findViewById(R.id.tvEsecuzione);

        bttSalvaParcheggio.setEnabled(true);//Nel dubbio inizializzo il bottone per renderelo premibile, visto che da altre parti disabilito la cosa

        final Posizione posizione = new Posizione(this.context);
        //Gli faccio prendere la posizione almeno una volta
        posizione.aggiornaGPS(500, 1);

        Timer timerAggiornamento= new Timer();
        TimerTask timerTaskAggiornamento=new TimerTask() {
            @Override
            public void run() {
                //Non faccio nulla
            }
        };

        timerAggiornamento.scheduleAtFixedRate(timerTaskAggiornamento, 1000, 1000);
        posizione.prendiPosizione();
        final AsyncTaskEsecuzione asyncTaskEsecuzione = new AsyncTaskEsecuzione(this.context, bttAnnulla, bttSalvaParcheggio, posizione);

        Intent intent = new Intent(this, ServiceEsecuzione.class);

        intent.putExtra("posizione", posizione);
      //  startForegroundService(intent);

        asyncTaskEsecuzione.execute();

        bttAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncTaskEsecuzione.cancel(true);

                //context.stopService(new Intent(context, ServiceEsecuzione.class));
                //Smetto di aggiornare costantemente la mia posizione
                posizione.fermaAggiornamentoGPS();

                Intent i = new Intent(context.getString(R.string.MAIN_TO_HOME));
                context.startActivity(i);

            }
        });


        bttSalvaParcheggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ho notato che ci mette un po' ad eseguire, quando è premuto non voglio che l'utente lo prema di nuovo interrompendo
                //l'algoritmo solo perchè sembra troppo lento
                bttSalvaParcheggio.setEnabled(false);

                tvErrore.setVisibility(View.INVISIBLE);//Inizializzo sempre il mio log di errore a invisible

                //Salvo i dati prima di cancellare l'async task
                posizione.prendiPosizione();
                final double[] coordinate = posizione.coordinate;
                asyncTaskEsecuzione.cancel(true);
                //context.stopService(new Intent(getApplicationContext(), ServiceEsecuzione.class));

                Toast.makeText(context, "Salvataggio in corso", Toast.LENGTH_LONG).show();


                Log.i("esecuzioneSalva", posizione.coordinate[0] + "spazio" + posizione.coordinate[1]);


                //Recupero username e password per capire di chi è l'account
                SharedPreferences sharedPreferences = getSharedPreferences("USERNAME_PASSWORD", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("USERNAME", "");
                String password = sharedPreferences.getString("PASSWORD", "");

                String[] nomiJson={"thing", "feature", "device", "location", "samples"};
                JSONObject location=new JSONObject();

                try {
                    location.put("type", "Point");
                    location.put("coordinates", coordinate[0]);
                    location.accumulate("coordinates", coordinate[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray samples= new JSONArray();
                try {
                    samples.put(CreazioneJson.createJSONObject(new String[]{"values"}, 1588147128));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject postJson=new JSONObject();
                try {
                    postJson=CreazioneJson.createJSONObject(nomiJson, username+"_"+password, "parking", "parking-app", location, samples);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, coordinate[0]+","+coordinate[1], Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Server.Post("/v1/measurements ", new Callback() {
                        @Override
                        public void onSuccess(JSONObject result) throws JSONException, IOException, InterruptedException {

                            posizione.fermaAggiornamentoGPS();

                            Server.reverseGeocoding(context, coordinate, new Callback() {
                                @Override
                                public void onSuccess(JSONObject response) throws JSONException, IOException, InterruptedException {


                                    JSONArray results = response.getJSONArray("results");
                                    JSONObject formatted_address = results.getJSONObject(1);
                                    String città_via = formatted_address.getString("formatted_address");
                                    Log.i("NOME CITTA_VIA", città_via);

                                    //Prendo nome Via
                                    JSONObject oggetto_riposta = results.getJSONObject(0);
                                    JSONArray address_components=oggetto_riposta.getJSONArray("address_components");
                                    JSONObject Via = address_components.getJSONObject(1);
                                    String via = Via.getString("long_name");
                                    Log.i("NOME VIA", via);

                                        Variabili.salvaParcheggio(context, città_via, via);
                                        Variabili.salvaCoordinate(context, coordinate);

                                        Intent i = new Intent(getString(R.string.FRAGMENT_PARCHEGGIO_TO_MOSTRA_SULLA_MAPPA));
                                        startActivity(i);

                                }

                                @Override
                                public void onError(ANError error) throws Exception {

                                    bttSalvaParcheggio.setEnabled(true);//Dato che non è andato a buon fine lo faccio premere di nuovo

                                    Log.e("Recupero Geocoding", "Errore recupero informazioni dal reverse geocoding col pulsante salva parcheggio");

                                    if(error.getErrorCode()==400 || error.getErrorCode()==403)
                                    {
                                        tvErrore.setText("Si è verificato un errore, riprova");
                                        Log.e("ESECUZIONE.errore", "Richiesta mal formulata");
                                    }
                                    else if(error.getErrorCode()==0 || error.getErrorCode()==500)
                                    {
                                        tvErrore.setText("Salvataggio non riuscito, il server non risponde");
                                        Log.e("ESECUZIONE.errore", "Erroe di Server");
                                    }
                                    else if(error.getErrorDetail().equals("connectionError"))
                                    {
                                        tvErrore.setText("Salvataggio non riuscito, verifica la connessione");
                                        Log.e("ESECUZIONE.errore", "Erroe di connessione");
                                    }
                                    tvErrore.setVisibility(View.VISIBLE);
                                }
                            });


                        }

                        @Override
                        public void onError(ANError error) throws Exception {

                            bttSalvaParcheggio.setEnabled(true);//Dato che non è andato a buon fine lo faccio premere di nuovo

                            if(error.getErrorCode()==400 || error.getErrorCode()==403)
                            {
                                tvErrore.setText("Si è verificato un errore, riprova");
                                Log.e("ESECUZIONE.errore", "Richiesta mal formulata");
                            }
                            else if(error.getErrorCode()==0 || error.getErrorCode()==500)
                            {
                                tvErrore.setText("Salvataggio non riuscito, il server non risponde");
                                Log.e("ESECUZIONE.errore", "Erroe di Server");
                            }
                            else if( error.getErrorDetail().equals("connectionError"))
                            {
                                tvErrore.setText("Salvataggio non riuscito, verifica la connessione");
                                Log.e("ESECUZIONE.errore", "Erroe di connessione");
                            }
                            tvErrore.setVisibility(View.VISIBLE);
                        }
                    }, context, postJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}


