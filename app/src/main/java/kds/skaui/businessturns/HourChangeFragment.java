package kds.skaui.businessturns;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class HourChangeFragment extends Fragment {
    EditText etHourCalendar, etCalendar;
    Button btnSearch;
    TextView tvExist;


    public HourChangeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hour_change, container, false);
        etCalendar = v.findViewById(R.id.etCalendar2);

        etHourCalendar = v.findViewById(R.id.etHourCalendar);
        btnSearch = v.findViewById(R.id.btnSearch);
        tvExist = v.findViewById(R.id.tvExist);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                HourChangeFragment.this.checkIfExist();
            }
        });
        return v;
    }

    void checkIfExist() {
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("Calendar");
        String theDate = etCalendar.getText().toString();
        String s1 = etHourCalendar.getText().toString();
        DatabaseReference tbf = dbf.child(theDate).child(s1);

        if (tbf != null) {
            tbf.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    //   String key = snap.getKey();
                    // Boolean value = snap.getValue(Boolean.class);
                    Task<Void> voidTask = tbf.setValue(false);
                    voidTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "שעה נחסמה בהצלחה", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}


