package com.parkingapp.homeactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class LoginOrSigninActivity extends Activity {
    Button login;
    Button signin;
    ImageView immagine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_or_signin);

        Intent i= getIntent();

        login=findViewById(R.id.bttLogIn);
        signin=findViewById(R.id.bttSignIn);
        immagine=findViewById(R.id.imageView);

        //Gestisco i bottoni per decidere se vogliamo registrarci o abbiamo già un account

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(getString((R.string.LOGINSIGNIN_TO_LOGIN)));
                startActivity(i);
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(getString((R.string.LOGINSIGNIN_TO_SIGNIN)));
                startActivity(i);
            }
        });
    }
}
