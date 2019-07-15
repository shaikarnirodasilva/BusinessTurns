package kds.skaui.businessturns;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class CancelFragment extends Fragment implements View.OnClickListener {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //Get The first and last name from the user database.
    final DatabaseReference turnRef = database.getReference("Turns");
    final DatabaseReference db = FirebaseDatabase.getInstance().getReference("messages");


    Button btnCancelTurn;
    String val = "", meow = "";
    final String theUID = LoginActivity.getTheUID();
    final String date = dateShared(val);
    final String time = timeShared(meow);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cancel, container, false);
        btnCancelTurn = v.findViewById(R.id.btnCancelTurn);
        btnCancelTurn.setClickable(false);
        btnCancelTurn.setVisibility(View.VISIBLE);
        //if the time and date arent null button is clickable, else no turns for the user .
        if (time != null && date != null) {
            btnCancelTurn.setClickable(true);
        } else {
            btnCancelTurn.setClickable(false);
        }

        btnCancelTurn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        try {
            DatabaseReference calendar = FirebaseDatabase.getInstance().getReference(workerSharedPrefs()).child("Calendar");
            turnRef.child(LoginActivity.getTheUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Turn value = dataSnapshot.getValue(Turn.class);

                        String date1 = value.getDate();
                        String time1 = value.getTime();
                        if (date1 == null && time1 == null) {
                            changeFrame(new MainFragment());
                            btnCancelTurn.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.getContextOfApplication(), "אין תור קיים", Toast.LENGTH_SHORT).show();
                        } else {
                            calendar.child(date1).child(time1).setValue(true);
                            cancelTurn();
                            btnCancelTurn.setVisibility(View.GONE);
                            //After canceling the turn remove the Date Time and Worker details of the last turn ,go to take another turn.
                            writeDate(null);
                            writeShared(null);
                            changeFrame(new MainFragment());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        btnCancelTurn.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.getContextOfApplication(), "אין תור קיים במקום לבזבז זמן תקבע תור!", Toast.LENGTH_SHORT).show();
                        changeFrame(new MainFragment());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Checks in database if there is any turn of the user, if not it shows no turn.
    void cancelTurn() {
        DatabaseReference child = turnRef.child(LoginActivity.getTheUID());
        child.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(CancelFragment.this.getContext(), "תורך נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
            }
        });
        db.child(LoginActivity.getTheUID()).removeValue();

    }

    void allFunction() {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Turns").child(theUID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Turn value = dataSnapshot.getValue(Turn.class);
                if (value != null) {
                    String date = value.getDate();
                    String time = value.getTime();
                    btnCancelTurn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelTurn();
                            DatabaseReference calendar = FirebaseDatabase.getInstance().getReference(workerSharedPrefs()).child("Calendar");
                            calendar.child(date).child(time).setValue(true);
                            btnCancelTurn.setVisibility(View.GONE);
                            //After canceling the turn remove the Date Time and Worker details of the last turn ,go to take another turn.
                            writeDate(null);
                            writeShared(null);
                            changeFrame(new MainFragment());
                        }
                    });

                }
                btnCancelTurn.setError("אין תור קיים");
                btnCancelTurn.setClickable(false);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void changeFrame(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        ft.replace(R.id.cancelFrag, fragment).commit();
    }

    private String workerSharedPrefs() {
        //1) get a reference to the shared preferences object. (singleton...Not new... getShared)
        SharedPreferences tk = getActivity().getSharedPreferences("WorkerName"/*xml file name...*/, Context.MODE_PRIVATE);
        //int val = obj.getIntValue()
        //3) return val
        return tk.getString("workerFirstName", ""/*defaultValue*/);
    }

    private String timeShared(String time) {
        //1) get a reference to the shared preferences object. (singleton...Not new... getShared)
        try {
            SharedPreferences tk = getActivity().getSharedPreferences("Time"/*xml file name...*/, Context.MODE_PRIVATE);
            time = tk.getString("time", ""/*defaultValue*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    private String dateShared(String value) {
        //1) get a reference to the shared preferences object. (singleton...Not new... getShared)
        try {
            SharedPreferences tk = getActivity().getSharedPreferences("Date"/*xml file name...*/, Context.MODE_PRIVATE);
            value = tk.getString("date", ""/*defaultValue*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private void writeShared(String b) {
        //1) reference to the shared object (sharedPreferences)
        //singleton...? No new...?
        //allows us to Read data:
        SharedPreferences tk = MainActivity.getContextOfApplication().getSharedPreferences("Time"/*xml file name...*/, Context.MODE_PRIVATE);
        //2) reference to the editor of the sharedPreferences
        //Writer
        SharedPreferences.Editor editor = tk.edit();
        //3) editor.put...(key, value).
        editor.putString("time", b);
        editor.apply();//new Thread -> save();
    }

    private void writeDate(String b) {
        //1) reference to the shared object (sharedPreferences)
        //singleton...? No new...?
        //allows us to Read data:
        SharedPreferences tk = MainActivity.getContextOfApplication().getSharedPreferences("Date"/*xml file name...*/, Context.MODE_PRIVATE);
        //2) reference to the editor of the sharedPreferences
        //Writer
        SharedPreferences.Editor editor = tk.edit();
        //3) editor.put...(key, value).
        editor.putString("date", b);
        editor.apply();//new Thread -> save();
    }
}


