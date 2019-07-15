package kds.skaui.businessturns;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by shaikarniro on 23.1.2018.
 */

class FreeDateDataSource {
    final static DateTimeFormatter dff = DateTimeFormat.forPattern("yyyy MM dd");
    Context context;

    FreeDateDataSource() {
    }


    //observer design pattern: loosely couple the listener.
    public interface OnFreeDateArrivedListener {
        void onFreeDateArrived(@Nullable ArrayList<String> todayTurns, @Nullable Exception e);
        //void onError(Exception e);
    }

    static void getFreeTurns(final FreeDateDataSource.OnFreeDateArrivedListener listener) {


        final LocalDate localDate = new LocalDate();
        String format = String.format(localDate.toDate().toString(), dff);

        String mShared = getPreferences(MainActivity.getContextOfApplication(), "workerFirstName");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference(mShared);
        database.child("Calendar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> todayDates = new ArrayList<>();

                //get 30 days in calendar.
                // Result Will Hold Here!!
                for (final DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String d = dsp.getKey();
                    if (d.contains("_")) {
                        String replace = d.replace("_", " ");
                        //add result into array list
                        //String format1 = String.format(String.valueOf(s), dff);
                        todayDates.add(replace);

                    }

                }

                while (todayDates.size() > 25) {
                    todayDates.remove(0);


                }
                listener.onFreeDateArrived(todayDates, null);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFreeDateArrived(null, new RuntimeException(" " + database.toString()));

            }
        });
    }

    private static String getPreferences(Context context, String keyValue) {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = context.getSharedPreferences("WorkerName", Context.MODE_PRIVATE);
        return sp.getString(keyValue, "");
    }


}


