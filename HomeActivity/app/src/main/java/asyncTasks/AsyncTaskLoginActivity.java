package asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.parkingapp.homeactivity.LogInActivity;
import com.parkingapp.homeactivity.R;
import com.parkingapp.homeactivity.SigninActivity;

import Server.Callback;

import org.json.JSONException;
import org.json.JSONObject;


import Server.WSComunication;
import mist.Variabili;

public class AsyncTaskLoginActivity extends AsyncTask<String, String, Object> {

  private Context context=null;
  private ProgressBar progressBar=null;
  private CheckBox checkBox=null;
  private TextView messaggioErrore=null;

  public  AsyncTaskLoginActivity(Context context, ProgressBar pb, CheckBox cb, TextView errore)
  {
      this.context=context;
      this.progressBar=pb;
      this.checkBox=cb;
      this.messaggioErrore=errore;
  }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(final String...strings) {

      final String username= strings[0];
      final String password = strings[1];

        try {
            Server.Server.Get("/v1/things/"+username+"_"+password, new Callback() {
                @Override
                public void onSuccess(JSONObject result) throws JSONException {

                    Log.i("GET ASYNC LOGIN:", result.toString());

                    Variabili.salvaUsernamePassword(context, strings);
                    Variabili.salvaRicordaUtente(context, checkBox.isChecked());
                    Variabili.aggiornaPosizione(context);

                    //Il passaggio di activity lo faccio nell'async task perchè se no ho notato che l'utente deve premere due volte il pulsante
                    Intent i = new Intent(context.getString(R.string.MAIN_TO_HOME));
                    WSComunication wsComunication = new WSComunication();
                    wsComunication.startWS(context);
                    context.startActivity(i);

                }
                @Override
                public void onError(ANError error) throws Exception {

                    Log.e("CALLBACK MAKE GET", "ERRORE NELLA CALL BACK DELLA MAKE GET, In Login Activity");
                    if(error.getErrorCode()==404)
                    {
                        messaggioErrore.setText("Username/Password errati");
                        messaggioErrore.setVisibility(View.VISIBLE);
                    }

                    else if(error.getErrorDetail().equals("connectionError"))
                    {
                        messaggioErrore.setText("Connessione ad internet assente");
                        messaggioErrore.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        messaggioErrore.setText("Si è verificato un errore, riprova");
                        messaggioErrore.setVisibility(View.VISIBLE);
                    }
                }
            }, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
