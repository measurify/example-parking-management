package fragment_home_activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.parkingapp.homeactivity.R;

import java.util.ArrayList;

import mist.Variabili;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PromemoriaNotifica#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PromemoriaNotifica extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView tvOrarioNotifica=null;
    Button btOrarioNotifica=null;
    ListView  lvOrari=null;
    Boolean premuto=false; //variabile per capire lo stato del menu con gli orari


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PromemoriaNotifica() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PromemoriaNotifica.
     */
    // TODO: Rename and change types and number of parameters
    public static PromemoriaNotifica newInstance(String param1, String param2) {
        PromemoriaNotifica fragment = new PromemoriaNotifica();
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
        return inflater.inflate(R.layout.fragment_promemoria_notifica, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvOrarioNotifica=view.findViewById(R.id.tvOrarioNotifica);
        btOrarioNotifica=view.findViewById(R.id.spOrarioNotifica);
        lvOrari=view.findViewById(R.id.lvListaOrariNotifica);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PROMEMORIA_NOTIFICA", Context.MODE_PRIVATE);
        String orario=sharedPreferences.getString("STATO", "1 ora");
        tvOrarioNotifica.setText(orario);

        //Creo la lista con tutti gli orari disponibili
        final ArrayList<String> arrayList= new ArrayList<>();
        arrayList.add("1 ora");
        arrayList.add("2 ore");
        arrayList.add("4 ore");
        arrayList.add("Un giorno");

        //Adatto il formato della lista alla mia schermata
        ArrayAdapter arrayAdapter=new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, arrayList);
        lvOrari.setAdapter(arrayAdapter);

        lvOrari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvOrarioNotifica.setText("  "+arrayList.get(position) );
                btOrarioNotifica.setBackgroundResource(R.drawable.menu_orari);
                lvOrari.setVisibility(View.INVISIBLE); //Dopo aver premuto la lista torna invisibile

                String orario= tvOrarioNotifica.getText().toString();
                long time_millisecond;
                switch (orario)
                {
                    case "  1 ora":
                        time_millisecond= 3600000;
                        Log.i("Tempo switch", "3600000");
                        break;

                    case "  2 ore":
                        time_millisecond= 7200000;
                        Log.i("Tempo switch", "7200000");
                        break;

                    case "  4 ore":
                        time_millisecond= 14400000;
                        Log.i("Tempo switch", "14400000");
                        break;

                    case "  Un giorno":
                        time_millisecond= 86400000;
                        Log.i("Tempo switch", "86400000");
                        break;

                    default:
                        time_millisecond= 3600000;
                        Log.i("Tempo switch", "3600000");
                        break;

                }
                Variabili.salvaPromemoriaNotifica(getContext(), orario, time_millisecond);
            }
        });


        btOrarioNotifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!premuto) {
                    btOrarioNotifica.setBackgroundResource(R.drawable.menu_orari_up);
                    lvOrari.setVisibility(View.VISIBLE); //Rendo visibile la lista con gli orari disponibili
                    premuto=true;
                }
                else
                {
                    btOrarioNotifica.setBackgroundResource(R.drawable.menu_orari);
                    lvOrari.setVisibility(View.INVISIBLE);
                    premuto=false;
                }
            }
        });

    }

}
