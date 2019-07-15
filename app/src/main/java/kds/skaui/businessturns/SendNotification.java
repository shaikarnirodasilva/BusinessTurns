package kds.skaui.businessturns;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by shaikarniro on 1.4.2018.
 */

public class SendNotification extends FirebaseInstanceIdService {

    private static final String TAG = "Token";

    public SendNotification() {
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


    }

    void sendSsmms() {
        //Sends an SMS to the client .
        final Turn turn = MainActivity.turn;
        String dateChoosed = turn.getDate();
        String timeChoosed = turn.getTime();
        try {

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
