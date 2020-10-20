package RichiesteConferma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.parkingapp.homeactivity.R;

import mist.Variabili;

public class EliminaParcheggio extends Activity {
    Button bttConferma=null;
    Button bttAnnulla=null;
    Context context= this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.richiesta_conferma);

        final Intent intent= getIntent();

        bttConferma=findViewById(R.id.btConfermaUscitaUtente);
        bttAnnulla=findViewById(R.id.btAnnullaUscitaUtente);



        bttAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(getString(R.string.MAIN_TO_HOME));
                startActivity(i);
            }
        });

        bttConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Variabili.salvaParcheggio(context, "Nessun parcheggio salvato", null);
                Variabili.salvaImpedimento(context, "Nessun impedimento registrato");

                //AGGIUNGERE CHIAMATA AL SERVER PER CANCELLARE IL PARCHEGGIO

                Intent i= new Intent(getString(R.string.MAIN_TO_HOME));
                startActivity(i);
            }
        });

    }
}
