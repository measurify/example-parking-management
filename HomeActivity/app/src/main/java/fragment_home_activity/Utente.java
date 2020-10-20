package fragment_home_activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parkingapp.homeactivity.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Utente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Utente extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button btEsci=null;
    TextView tvUsername=null;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Utente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Utente.
     */
    // TODO: Rename and change types and number of parameters
    public static Utente newInstance(String param1, String param2) {
        Utente fragment = new Utente();
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
        return inflater.inflate(R.layout.fragment_utente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btEsci=view.findViewById(R.id.btExit_Utente);
        tvUsername=view.findViewById(R.id.tvFragmentUtente);

        //Associo nome username scelto nella signin/login
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USERNAME_PASSWORD", Context.MODE_PRIVATE);
        String username=sharedPreferences.getString("USERNAME", "");
        tvUsername.setText(username);


        btEsci.setOnClickListener(new View.OnClickListener() { //Passaggio a activity richiesta conferma
            @Override
            public void onClick(View v) {
                Intent i= new Intent (getString(R.string.EXIT_OR_DELETE));
                startActivity(i);
            }
        });

    }
}
