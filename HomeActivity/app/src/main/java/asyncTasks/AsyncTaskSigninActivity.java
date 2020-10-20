package asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.androidnetworking.error.ANError;
import com.parkingapp.homeactivity.HomeActivity;
import com.parkingapp.homeactivity.R;
import com.parkingapp.homeactivity.SigninActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import Server.Callback;

import Server.Server;
import Server.WSComunication;
import Server.CreazioneJson;
import mist.Variabili;


public class AsyncTaskSigninActivity extends AsyncTask<String, Integer, Integer> {

    private Context context=null;
    private ProgressBar progressBar=null;
    private TextView messaggioErrore=null;



    public AsyncTaskSigninActivity(ProgressBar pb,TextView errore,Context context){
        this.context=new WeakReference<>(context).get();
        this.progressBar=pb;
        this.messaggioErrore=errore;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressBar.setVisibility(View.VISIBLE);

    }


    @Override
    protected Integer doInBackground(final String...strings) {

        String url= "/v1/things/";
        String username=strings[0];
        String password = strings[1];

        String nomiJson[]={"_id"};
        JSONObject oggettoJson= null;
        try {
            oggettoJson = CreazioneJson.createJSONObject(nomiJson, username+"_"+password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Server.Post(url, new Callback() {
               @Override
               public void onSuccess(JSONObject result) throws JSONException {

                   Log.i("POST ASYNC SINGNIN:", result.toString());


                   //Inizializzo tutte le variabili che voglio per un account nuovo
                   Variabili.salvaUsernamePassword(context, strings);
                   Variabili.salvaPromemoriaNotifica(context, "1 ora", 3600000);
                   Variabili.salvaParcheggio(context, "Nessun parcheggio salvato", null);
                   Variabili.salvaImpedimento(context, "Nessun impedimento registrato");

                   //Il passaggio di activity lo faccio nell'async task perchè se no ho notato che l'utente deve premere due volte il pulsante
                   Intent i = new Intent(context.getString(R.string.MAIN_TO_HOME));
                   WSComunication wsComunication = new WSComunication();
                   wsComunication.startWS(context);
                   context.startActivity(i);
               }

               @Override//Non restituisce numeri, ma la gestice così
               public void onError(ANError error) throws Exception
               {
                   Log.e("CALLBACK MAKE POST", "ERRORE NELLA CALL BACK DELLA MAKE POST, In Signin Activity");
                   if(error.getErrorCode()==400)
                   {
                       messaggioErrore.setText("Username già in uso, prova con un altro");
                       messaggioErrore.setVisibility(View.VISIBLE);
                   }

                   else if(error.getErrorDetail().equals("connectionError"))
                   {
                       messaggioErrore.setText("Connessione ad internet assente");
                       messaggioErrore.setVisibility(View.VISIBLE);
                   }

                 else  {
                       messaggioErrore.setText("Si è verificato un errore, riprova");
                       messaggioErrore.setVisibility(View.VISIBLE);
                    Log.e("ParseError", "Errore server asyncTask signin activity");
                }


               }
           }, this.context, oggettoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  0;
    }



    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);

        progressBar.setVisibility(View.INVISIBLE);

    }
}
