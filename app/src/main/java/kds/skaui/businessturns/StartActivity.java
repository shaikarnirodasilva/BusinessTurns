package kds.skaui.businessturns;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

public class StartActivity extends Activity {
    private DateTime now = DateTime.now();
    static boolean koko, kiki;
    final static DateTimeFormatter dff = DateTimeFormat.forPattern("yyyy_MM_dd");
    private DateTime dateTime = new DateTime();
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    checkPermissions();
                    //adds one more day every day .
                    DayOfWeek d = new DayOfWeek();
                    super.run();
                    sleep(5000);  //Delay of 10 seconds
                } catch (Exception e) {

                } finally {

                    Intent i = new Intent(StartActivity.this,
                            LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }

    //settles the dateTime variable as in the Firebase Database.
    private final String hebrewDays(DateTime dTime) {
        String replace = "";
        String asText = dTime.dayOfWeek().getAsText();

        if (asText.equals("יום ראשון")) {
            replace = asText.replace("יום ראשון", "ראשון");
        }
        if (asText.equals("יום שני")) {
            replace = asText.replace("יום שני", "שני");
        }
        if (asText.equals("יום שלישי")) {
            replace = asText.replace("יום שלישי", "שלישי");
        }
        if (asText.equals("יום רביעי")) {
            replace = asText.replace("יום רביעי", "רביעי");
        }
        if (asText.equals("יום חמישי")) {
            replace = asText.replace("יום חמישי", "חמישי");
        }
        if (asText.equals("יום שישי")) {
            replace = asText.replace("יום שישי", "שישי");
        }
        return replace;
    }

    //Initialize only once the calendar .
    private void initializeCalendar() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("0509596040");
        if (db == null) {
            final DayOfWeek dayOfWeek = new DayOfWeek(10, 0, 19, 0);

        } else {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        final DayOfWeek dayOfWeek = new DayOfWeek(10, 0, 19, 0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }






    //Asks for required permissions.
    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(StartActivity.this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(StartActivity.this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this,
                    android.Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{android.Manifest.permission.SEND_SMS},
                        1);

                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            System.out.println("We Have Permission!");
        }
    }

}
