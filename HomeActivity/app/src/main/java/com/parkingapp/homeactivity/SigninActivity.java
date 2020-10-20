package com.parkingapp.homeactivity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import asyncTasks.AsyncTaskSigninActivity;
import mist.Variabili;

public class SigninActivity extends Activity {


    Button btRegistrati=null;
    EditText etUsername=null;
    EditText etPassword=null;
    EditText etPasswordConferma=null;
    ImageButton imBackBottone=null;
    ProgressBar pbProgressBarSignin=null;
    TextView tvErrore=null;

    private static boolean codice=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);


        Intent i= getIntent();

        btRegistrati=findViewById(R.id.bttRegistrati);
        etPassword=findViewById(R.id.etSignin_Password);
        etPasswordConferma=findViewById(R.id.etSignin_Password_Conferma);
        imBackBottone=findViewById(R.id.bttBackSignin_to_LoginSignin);
        pbProgressBarSignin=findViewById(R.id.pbSignin);
        etUsername= findViewById(R.id.etSignin_Username);
        tvErrore=findViewById(R.id.tvErrore_Signin);

        final Context context=this;

        imBackBottone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getString(R.string.MAIN_TO_LOGSIGN)); //Torno alla schermata di prima (Login/Signin)
                startActivity(intent);
            }
        });

        btRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password= etPassword.getText().toString();
                String password_conferma= etPasswordConferma.getText().toString();
                String username= etUsername.getText().toString();

                tvErrore.setVisibility(View.INVISIBLE); //Metto invisibile il log di errore

                if(!password.equals("") && !username.equals("")) {

                    if (password.equals(password_conferma)) {

                        if(username.length()<=25 && password.length()<=25)
                        {
                            //Fine controlli, inzio body

                            AsyncTaskSigninActivity asyncTaskSigninActivity= new AsyncTaskSigninActivity(pbProgressBarSignin,tvErrore ,context);
                            String parametri[]={username, password};
                            asyncTaskSigninActivity.execute(parametri);

                            //Il passaggio di activity lo faccio nell'async task perchè se no ho notato che l'utente deve premere due volte il pulsante

                        }
                        else
                        {

                            tvErrore.setText("Username/Password devono essere max 25 caratteri");
                            tvErrore.setVisibility(View.VISIBLE);
                        }
                    }

                    else {
                        tvErrore.setText("Le password non corrispondono");
                        tvErrore.setVisibility(View.VISIBLE);
                    }
                }
                else
                {

                    tvErrore.setText("I campi Username e Password non possono essere vuoti");
                    tvErrore.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}
