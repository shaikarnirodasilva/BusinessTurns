package kds.skaui.businessturns;


import android.*;
import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FreeTimeShowFragment extends Fragment implements FreeTimeDataSource.OnFreeTurnArrivedListener {
    Button btnSaveTurn;
    Context mContext = MainActivity.contextOfApplication;

    public FreeTimeShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_free_time_show, container, false);
        FreeTimeDataSource.getFreeTurns(this);
        return v;
    }

    @Override
    public void onFreeTurnArrived(@Nullable ArrayList<String> todayTurns, @Nullable Exception e) {
        getActivity().runOnUiThread(() -> {
            if (todayTurns != null) {
                updateUI(todayTurns);

            } else if (e != null) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void updateUI(ArrayList<String> freeTime) {
        View v = getView();
        assert v != null;
        RecyclerView rcvFreeTime = v.findViewById(R.id.rcvFreeTime);
        //the adapter takes movies and provides Views for the movies.
        //2) MoviesAdapter adapter = new Movies adapter (movies, context)
        FreeTurnAdapter adapter = new FreeTurnAdapter(freeTime, getActivity());

        //3) recycler -> take the adapter.
        rcvFreeTime.setAdapter(adapter);

        //4)
        rcvFreeTime.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private static String getTime() {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Time", Context.MODE_PRIVATE);
        return sp.getString("time", "");
    }

    //

    private static String datePreferences() {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Date", Context.MODE_PRIVATE);
        return sp.getString("date", "");
    }

    //Change frame.
    void changeFrame(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        ft.replace(R.id.turnSave, fragment).commit();
    }


    private void sendSms() {
        final Turn turn = MainActivity.turn;
        String dateChoosed = turn.getDate();
        String timeChoosed = turn.getTime();
        try {
            //TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            String fireUser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            SmsManager smsManager = SmsManager.getDefault();
            String message = "שלום ותודה שקבעת תור למספרה שלנו,תורך נקבע לתאריך " + dateChoosed + "בשעה" + timeChoosed;
            smsManager.sendTextMessage("+9720509596040", null, message, null, null);
            Toast.makeText(MainActivity.getContextOfApplication(), "נשלחה הודעה למכשירך", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.getContextOfApplication(), "קרתה תקלה בשליחת הסמס נא לפנות לעדיאל", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}


