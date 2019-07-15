package kds.skaui.businessturns;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by shaikarniro on 23.1.2018.
 */

public class FreeTimeDataSource {
    final static DateTimeFormatter dff = DateTimeFormat.forPattern("yyyy MM dd");
    private static ArrayList<String> todayTurns = new ArrayList<String>();

    FreeTimeDataSource(ArrayList<String> todayTurns) {
        FreeTimeDataSource.todayTurns = todayTurns;
    }

    //observer design pattern: loosely couple the listener.
    public interface OnFreeTurnArrivedListener {
        void onFreeTurnArrived(@Nullable ArrayList<String> todayTurns, @Nullable Exception e);
        //void onError(Exception e);
    }

    static void getFreeTurns(final FreeTimeDataSource.OnFreeTurnArrivedListener listener) {
        final String date = datePrefernces();
        final String format = String.format(date, dff);

        String mShared = getWorkerName(MainActivity.getContextOfApplication());

        DatabaseReference database = FirebaseDatabase.getInstance().getReference(mShared).child("Calendar");
        database.child(format).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //the boolean.
//                Boolean isTrue =dsp.getValue(Boolean.class);
                //the hour.
                //if the boolean is true --> turn is free get it and add it to array.
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                    String value = String.valueOf(dsp.getValue(true));
                    String key = dsp.getKey();
                    if (value.equals("true")) {
                        todayTurns.add(key);
                        //Toast.makeText(, key, Toast.LENGTH_SHORT).show();
                        Log.d("SHay", key);
                    }
                }
                listener.onFreeTurnArrived(todayTurns, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private static String getWorkerName(Context context) {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = context.getSharedPreferences("WorkerName", Context.MODE_PRIVATE);
        return sp.getString("workerFirstName", "");
    }

    private static String datePrefernces() {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Date", Context.MODE_PRIVATE);
        return sp.getString("date", "");
    }
}
