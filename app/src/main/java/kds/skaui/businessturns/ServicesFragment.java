package kds.skaui.businessturns;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;


import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ServicesFragment extends Fragment {
    final private String[] servicesName = {"תספורת רגילה   ", "תספורת וזקן ", "החלקה יפנית/משקמת", "צבע וציורי ראש "};
    final private String[] femaleServices = {"תספורת רגילה ", "החלקה יפנית/משקמת ", "פן", "צבע"};
    private DateTime now = DateTime.now();
    private DateTime dateTime = new DateTime();
    private int openingMinute;
    private int openingHour;
    private int y = now.getYear();
    private int m = now.getMonthOfYear();
    private int d = now.getDayOfMonth();
    private DateTime calendarTime2 = new DateTime(y, m, d, openingHour, openingMinute);
    final static DateTimeFormatter dff = DateTimeFormat.forPattern("yyyy_MM_dd");

    TextView tvService1;
    TextView tvService2;
    TextView tvService3;
    TextView tvService4;
    TextView tvDateTime;
    TextView tvDateTime2;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_services, container, false);

        tvService1 = v.findViewById(R.id.tvService4);
        tvService2 = v.findViewById(R.id.tvService1);
        tvService3 = v.findViewById(R.id.tvService3);
        tvService4 = v.findViewById(R.id.tvService2);
        tvDateTime = v.findViewById(R.id.tvDateTime);
        tvDateTime2 = v.findViewById(R.id.tvDateTime2);
        servicesInflater();


        //if the user already have a turn he cant push the buttons.
        tvService1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v14) {
                ServicesFragment.this.checkIfTurnExisted((boolean hasTurns) -> {
                    if (!hasTurns) {
                        ServicesFragment.this.serviceClicked(new FreeDateShowFragment());
                        final String turnKind = tvService1.getText().toString();
                        SharedPreferences tk = ServicesFragment.this.getActivity().getSharedPreferences("TurnKind"/*xml file name...*/, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = tk.edit();
                        editor.putString("firstName", turnKind);
                        editor.apply();//new Thread -> save();
                    }
                });
            }
        });
        tvService2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v13) {
                ServicesFragment.this.checkIfTurnExisted(new ShayBooleanListener() {
                    @Override
                    public void result(boolean hasTurns) {
                        if (!hasTurns)
                            ServicesFragment.this.serviceClicked(new FreeDateShowFragment());
                        final String turnKind = tvService2.getText().toString();
                        //1) reference to the shared object (sharedPreferences)
                        //singleton...? No new...?
                        //allows us to Read data:
                        SharedPreferences tk = ServicesFragment.this.getActivity().getSharedPreferences("TurnKind"/*xml file name...*/, Context.MODE_PRIVATE);
                        //2) reference to the editor of the sharedPreferences
                        //Writer
                        SharedPreferences.Editor editor = tk.edit();
                        //3) editor.put...(key, value).
                        editor.putString("firstName", turnKind);
                        editor.apply();//new Thread -> save();
                    }
                });
            }
        });
        tvService3.setOnClickListener(v1 -> checkIfTurnExisted(hasTurns -> {
            if (!hasTurns)
                serviceClicked(new FreeDateShowFragment());
            final String turnKind = tvService3.getText().toString();
            //1) reference to the shared object (sharedPreferences)
            //singleton...? No new...?
            //allows us to Read data:
            SharedPreferences tk = getActivity().getSharedPreferences("TurnKind"/*xml file name...*/, Context.MODE_PRIVATE);
            //2) reference to the editor of the sharedPreferences
            //Writer
            SharedPreferences.Editor editor = tk.edit();
            //3) editor.put...(key, value).
            editor.putString("firstName", turnKind);
            editor.apply();//new Thread -> save();
        }));
        tvService4.setOnClickListener(v12 -> checkIfTurnExisted(hasTurns -> {
            if (!hasTurns)
                serviceClicked(new FreeDateShowFragment());
            final String turnKind = tvService4.getText().toString();
            //1) reference to the shared object (sharedPreferences)
            //singleton...? No new...?
            //allows us to Read data:
            SharedPreferences tk = getActivity().getSharedPreferences("TurnKind"/*xml file name...*/, Context.MODE_PRIVATE);
            //2) reference to the editor of the sharedPreferences
            //Writer
            SharedPreferences.Editor editor = tk.edit();
            //3) editor.put...(key, value).
            editor.putString("firstName", turnKind);
            editor.apply();//new Thread -> save();
        }));

        return v;

    }

    //Writes the name of the services to the text view
    final void servicesInflater() {
        String read = read();
        if (read.equals("גבר")) {
            tvService1.setText(servicesName[0]);
            tvService2.setText(servicesName[1]);
            tvService3.setText(servicesName[2]);
            tvService4.setText(servicesName[3]);
        } else if (read.equals("אישה")) {
            tvService1.setText(femaleServices[0]);
            tvService2.setText(femaleServices[1]);
            tvService3.setText(femaleServices[2]);
            tvService4.setText(femaleServices[3]);
        }

    }

    //Change frame.
   final void serviceClicked(Fragment fragment) {
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.services_fragment, fragment).
                commit();
    }


    void checkIfTurnExisted(ShayBooleanListener listener) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Turns").child(LoginActivity.getTheUID());

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //we got a response from the server!
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Toast.makeText(getContext(), "תור קיים כבר נא למחוק", Toast.LENGTH_SHORT).show();
                    listener.result(true);
                    return;
                }

                //if we got here... No Turns For the User by Uid.
                listener.result(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.result(false);
            }
        });
    }

    private String read() {
        SharedPreferences sp = getActivity().getSharedPreferences("gender", Context.MODE_PRIVATE);
        final String s = sp.getString("genderKind", "גבר");
        return s;
    }

}
