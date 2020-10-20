package asyncTasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.FontsContract;
import android.util.Log;
import android.widget.Button;


import androidx.annotation.RequiresApi;
import com.parkingapp.homeactivity.R;

import java.util.Timer;
import java.util.TimerTask;

import Accelerometro.Accelerometro;
import Notifica.Notifica;
import Posizione.Posizione;
import mist.Variabili;


public class AsyncTaskEsecuzione extends AsyncTask{




    Context context;
    Button annulla;
    Button registraParcheggio;
     Posizione posizione;
    String città_destinazione=null;

    //Mi faccio passare anche un oggetto posizione così è sempre lo stesso e posso decidere come modificare la precisione del GPS
    public AsyncTaskEsecuzione(Context context, Button annulla, Button registraParcheggio, Posizione posizione)
    {
        this.context=context;
        this.annulla=annulla;
        this.registraParcheggio=registraParcheggio;
        this.posizione=posizione;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //Il GPS si aggiorna ogni 10 minuti e dopo almeno 400 metri di distanza
        posizione.aggiornaGPS(600000, 400);

        posizione.prendiPosizione();

        SharedPreferences sharedPreferences = context.getSharedPreferences("DESTINAZIONE_VIAGGIO", Context.MODE_PRIVATE);
        città_destinazione = sharedPreferences.getString("DESTINAZIONE_VIAGGIO", "");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected Object doInBackground(Object[] objects) {

        boolean esecuzione_città=true;
        boolean esecuzione_fermo=true;
        boolean esecuzione_sensore=true;

        final boolean[] esecuzioneTimerTask = {false};


        Accelerometro accelerometro= new Accelerometro(context);

        //0:nome intero, 1 città, 2 via
        final String[][] posizione_via_città = {{null, null, null}};
        String[] città_attuale={null};




        //Per far vibrare il cellulare quando registra il parcheggio
        Vibrator vibrazione=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        //Utilizzo un timer per tenere il tempo che devo aspetare prima di nuovi controlli
        Timer myTimer = new Timer();

        try {
            posizione_via_città[0] =posizione.nomeViaECittà();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        

      //Primo step: controllo in un loop infinito di trovarmi nella città giusta
      while (esecuzione_città)
      {
          if(isCancelled())
          {
              this.cancel(true);
             break;
          }

          città_attuale[0] = posizione_via_città[0][1];
          assert città_attuale[0] != null;
          if(città_attuale[0].equals(città_destinazione))
          {
              //Aggiorno il GPS ogni minuto e dopo almeno 150 metri di distanza
              //(Soglia di 9 km/h sotto la quale avendo sempre le stesse coordinate, guardo il sensore accelertometrico)
              posizione.fermaAggiornamentoGPS();
              posizione.aggiornaGPS(60000, 150);

              final boolean[] fermo = {posizione.èFermo()};

              //Secondo step: verifichiamo che la macchina sia ferma
              while (esecuzione_fermo)
              {
                  if(isCancelled())
                  {
                      this.cancel(true);
                      break;
                  }

                  if(fermo[0])
                  {
                      //Ultimo step: verifico che il sensore accelerometrico mi dica se mi sono alzato
                      while (esecuzione_sensore)
                      {
                          if(isCancelled())
                          {
                              this.cancel(true);
                              break;
                          }

                          if(posizione.èFermo()) {
                              if (accelerometro.esegui()) {
                                  try {
                                      posizione.nomeViaECittà();
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }

                                  try {
                                      posizione_via_città[0] = posizione.nomeViaECittà();
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }

                                      //Ora che ho superato tutti i controlli salvo il parchegio
                                      double[] coordinate = posizione.coordinate;
                                      Variabili.salvaCoordinate(context, coordinate);

                                      Variabili.salvaParcheggio(context, posizione_via_città[0][0], posizione_via_città[0][2]);


                                      //Concludo mettendo a false tutti i loop
                                      esecuzione_città = false;
                                      esecuzione_fermo = false;
                                      esecuzione_sensore = false;

                                      //Faccio vibrare il cellulare per dare conferma che ho registrato il parcheggio
                                      assert vibrazione != null;
                                      vibrazione.vibrate(700);

                                      //Passo all'activity finale in cui mostro il parcheggio sulla mappa
                                      Intent i = new Intent(context.getString(R.string.FRAGMENT_PARCHEGGIO_TO_MOSTRA_SULLA_MAPPA));
                                      i.putExtra("Via", "Via: "+posizione_via_città[0][2]);
                                      context.startActivity(i);

                              }
                          }
                          //Faccio nuovamente la verifica perché se fosse ripartito torno al ciclo precedente
                          else if(!posizione.èFermo())
                          {
                              fermo[0]=false;
                              break;
                          }
                      }
                  }

                 else if(esecuzione_fermo && !esecuzioneTimerTask[0])
                  {

                      esecuzioneTimerTask[0]=true;
                          TimerTask timerTask=new TimerTask() {
                              @Override
                              public void run() {
                                  if(isCancelled())
                                  {
                                      esecuzioneTimerTask[0]=false;
                                      myTimer.cancel();
                                  }
                                  fermo[0] =posizione.èFermo();
                              }
                          };

                          //Riduco il ritardo e gli faccio fare più giri così da tenere sempre sotto controllo se blocco l'asyncTask premendo il pulsante
                          myTimer.scheduleAtFixedRate(timerTask, 36000, 1);

                      if(isCancelled())
                      {
                          break;
                      }
                  }

              }
          }

          //Se non siamo nella città giusta faccio attendere il sistema per 5 minuti
        else if(esecuzione_città && !esecuzioneTimerTask[0])//Il controllo è obbligatorio perché se ho finito non devo aspettare a caso
          {

              esecuzioneTimerTask[0] =true;

              TimerTask timerTask = new TimerTask() {
                  @Override
                  public void run() {
                      //Non faccio nulla
                      try {

                          if (isCancelled()) {
                              esecuzioneTimerTask[0] =false;
                              myTimer.cancel();
                              Log.i("TIMER esecuzione_città", "Timer concluso");
                          }

                          posizione_via_città[0] =posizione.nomeViaECittà();
                          esecuzioneTimerTask[0] =false;
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }

                  }
              };

              myTimer.scheduleAtFixedRate(timerTask, 300000, 1);


              if (isCancelled()) {
                  break;
              }

          }

      }

        return null;

    }



    @Override
    protected void onCancelled() {
        super.onCancelled();

    }



    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        posizione.fermaAggiornamentoGPS();

    }

}
