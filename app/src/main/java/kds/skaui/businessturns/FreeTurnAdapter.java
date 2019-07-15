package kds.skaui.businessturns;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by shaikarniro on 23.1.2018.
 */

public class FreeTurnAdapter extends RecyclerView.Adapter<FreeTurnAdapter.FreeTurnViewHolder> {
    private List<ContactsContract.Data> mDataset = new ArrayList<>();

    private ArrayList<String> freeTurns;
    private Context context;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference("messages");
    //inflater -> takes an xml as a parameter and Creates a fully fledged android View from it.
    private LayoutInflater inflater;

    //Constructor
    FreeTurnAdapter(ArrayList<String> freeTurns, Context context) {
        this.freeTurns = freeTurns;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public FreeTurnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.free_turn_item, parent, false);
        return new FreeTurnViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FreeTurnViewHolder holder, int position) {
        final String freeTurn = freeTurns.get(position);
        holder.tvHour.setText(freeTurn);
        holder.tvHour.setOnClickListener(v -> {
            MainActivity.turn.setIsTimer(true);
            String time = holder.tvHour.getText().toString();
            writeDateTime(time);


            //Checkings that the user clicked on a time and a date .
            makeTurnOccupied();
            newTurn();
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
            ft.replace(R.id.datesFree, new MainFragment()).commit();
            Toast.makeText(context, getTime() + " בשעה " + datePreferences(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return freeTurns.size();
    }

    class FreeTurnViewHolder extends RecyclerView.ViewHolder {
        //no encapsulation for efficiency:
        private TextView tvHour;
        View v;


        FreeTurnViewHolder(View v) {
            super(v);
            this.v = v;
            tvHour = v.findViewById(R.id.tvHour);
        }
    }

    private void makeTurnOccupied() {
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fireUser != null;
        final DatabaseReference calendars = FirebaseDatabase.getInstance().getReference("Calendar");
        String dateChoosed = datePreferences();
        String timeChoosed = getTime();

        FirebaseDatabase.getInstance().getReference(workerSharedPrefs()).child("Calendar").child(dateChoosed).child(timeChoosed).setValue("false");
    }

    private void newTurn() {
        /*FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fireUser != null;*/
        final DatabaseReference turns = FirebaseDatabase.getInstance().getReference("Turns");
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        final String dateChoosed = datePreferences();
        final String timeChoosed = getTime();
        //First Changes the turn time and day to false , so it wont show the turn anymore.
        //Writes the turn in Turns under the user Uid.
        //But first, take the user first and last name.
        usersRef.child(LoginActivity.getTheUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (u != null) {
                    String firstName = u.getFirstName();
                    String lastName = u.getLastName();
                    String phoneNumber = u.getPhoneNumber();
                    turns.child(LoginActivity.getTheUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = workerSharedPrefs();
                            if (s.equals("0509596040")) {
                                s = "יוחאי";
                            } else s = "עדיאל";

                            Turn newTurn = new Turn(firstName, lastName, dateChoosed, timeChoosed, readSharedPrefs(), workerSharedPrefs(), s, phoneNumber);
                            Task<Void> voidTask = turns.child(LoginActivity.getTheUID()).setValue(newTurn);
                            voidTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.getContextOfApplication(), "תורך נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //reads the Worker Name,Date,Time, and Turn Kind with Shared Preferences . :)

    private String workerSharedPrefs() {
        //1) get a reference to the shared preferences object. (singleton...Not new... getShared)
        SharedPreferences tk = MainActivity.getContextOfApplication().getSharedPreferences("WorkerName"/*xml file name...*/, Context.MODE_PRIVATE);
        //int val = obj.getIntValue()
        //3) return val
        return tk.getString("workerFirstName", ""/*defaultValue*/);
    }

    private String readSharedPrefs() {
        //1) get a reference to the shared preferences object. (singleton...Not new... getShared)
        SharedPreferences tk = MainActivity.getContextOfApplication().getSharedPreferences("TurnKind"/*xml file name...*/, Context.MODE_PRIVATE);
        //int val = obj.getIntValue()
        //3) return val
        return tk.getString("firstName", ""/*defaultValue*/);

    }

    private static String getTime() {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Time", Context.MODE_PRIVATE);
        return sp.getString("time", "");
    }

    private void writeDateTime(String var) {
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Time", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("time", var);
        edit.apply();//save

    }

    private static String datePreferences() {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Date", Context.MODE_PRIVATE);
        return sp.getString("date", "");
    }

}



