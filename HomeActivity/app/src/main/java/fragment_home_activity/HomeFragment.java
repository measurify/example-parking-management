package fragment_home_activity;

import android.app.AlertDialog;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parkingapp.homeactivity.R;

import java.util.ArrayList;

import mist.Variabili;

import static mist.Variabili.salvaDestinazione;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button bttAvvia=null;
    TextView tvScegliCittà=null;
    Button btMenu=null;
    ListView lvCittàDestinazione=null;
    TextView avviso1=null;
    TextView avviso2=null;
    Boolean premuto=false; //Parametro per capire lo stato del menu con le scelte delle città


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bttAvvia=view.findViewById(R.id.bttAvviaHomeFragment);
        tvScegliCittà=view.findViewById(R.id.tvCittàDestinazione);
        avviso1=view.findViewById(R.id.tvAvviso1);
        avviso2=view.findViewById(R.id.tvAvviso2);
        lvCittàDestinazione=view.findViewById(R.id.lvListaCittàDestinazione);
        btMenu=view.findViewById(R.id.btMenuCittàDestinazioneHomeFragment);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DESTINAZIONE_VIAGGIO", Context.MODE_PRIVATE);
        String orario=sharedPreferences.getString("DESTINAZIONE_VIAGGIO", " ----");
        tvScegliCittà.setText(orario);

        final ArrayList<String> arrayList= new ArrayList<>();

        String città_Server[] = getResources().getStringArray(R.array.Città_Server);

        for(int i=0; i<città_Server.length; i++)
        {
            arrayList.add(città_Server[i]);
        }

        //Adatto il formato della lista alla mia schermata
        ArrayAdapter arrayAdapter=new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, arrayList);
        lvCittàDestinazione.setAdapter(arrayAdapter);

        lvCittàDestinazione.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvScegliCittà.setText(""+arrayList.get(position) );
                btMenu.setBackgroundResource(R.drawable.menu_orari);
                lvCittàDestinazione.setVisibility(View.INVISIBLE); //Dopo aver premuto la lista torna invisibile
                bttAvvia.setVisibility(View.VISIBLE);
                Variabili.salvaDestinazione(getContext(), arrayList.get(position)); //Salvo la destinazione
            }
        });


        //Gestisco la visibilità menu a tendina con le città da scegliere
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!premuto)
                {
                    bttAvvia.setVisibility(View.INVISIBLE);
                    lvCittàDestinazione.setVisibility(View.VISIBLE);
                    premuto=true;
                    btMenu.setBackgroundResource(R.drawable.menu_orari_up);

                }
                else
                {
                    bttAvvia.setVisibility(View.VISIBLE);
                    lvCittàDestinazione.setVisibility(View.INVISIBLE);
                    premuto=false;
                    btMenu.setBackgroundResource(R.drawable.menu_orari);
                }
            }
        });

        //avvia il task principale dell'app
        bttAvvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getContext();
                String testo = tvScegliCittà.getText().toString();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MAPPE_SCARICATE", Context.MODE_PRIVATE);
                String mappa_scaricata = sharedPreferences.getString(testo, "");

                final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                final ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                //Controllo che l'utente abbia attivato il GPS e la connessione dati
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps(context);
                }

              else  if(activeNetworkInfo==null || !activeNetworkInfo.isConnected())
                {
                    buildAlertMessageNoInternet(context);
                }
                //Avvio l'esecuzione solo se i sensori sono attivi
                else {

                    //Passo all'activity di esecuzione
                    Intent i = new Intent(getString(R.string.HOME_FRAGMENT_TO_ESECUZIONE));
                    startActivity(i);
                }
            }
        });

    }

    //Se l'utente ha connessione dati o GPS spenti si attiva una finestra per chiedergli di attivarli, sensore serve per capire il tipo
    private void buildAlertMessageNoGps(Context context) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("È necessario abilitare il GPS per un corretto funzionamento dell'applicazione")
                    .setCancelable(false)
                    .setPositiveButton("Abilita", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
    }

    private void buildAlertMessageNoInternet(Context context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("È necessario abilitare la connessione dati per un corretto funzionamento dell'applicazione")
                .setCancelable(false)
                .setPositiveButton("Abilita", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
