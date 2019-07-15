package kds.skaui.businessturns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "HO";
    Button btnComplete, btnEnd;
    TextView tvPhone;
    EditText etLastName, etFirstName;
    EditText etPhone;
    private FirebaseAuth mAuth;
    static String uid;
    private FirebaseAuth.AuthStateListener authStateListener;

// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnEnd = findViewById(R.id.btnEnd);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnComplete = findViewById(R.id.btnComplete);
        etPhone = findViewById(R.id.etPhone);
        signInAnony();


        //Checkings if the phone number is already exists.
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.userExistCheck(new ShayBooleanListener() {
                    @Override
                    public void result(boolean hasTurns) {
                        if (hasTurns) {
                            //Number is exists
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class/*Where to?*/);
                            startActivity(intent);
                        } else if (!hasTurns) {
                            etFirstName.setVisibility(View.VISIBLE);
                            etLastName.setVisibility(View.VISIBLE);
                            btnEnd.setVisibility(View.VISIBLE);
                            btnComplete.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean b = false;
                if (checkFilled(b)) {
                    //if the user is not exist then write it to users again , else intent directly .
                    saveUser();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class/*Where to?*/);
                    startActivity(intent);
                } else
                    checkFilled(b
                    );
            }
        });
    }


    private void saveUser() {
        //1) get the current user (from firebase)

        if (etFirstName.getText() != null && etLastName.getText() != null) {
            btnComplete.setVisibility(View.VISIBLE);
            String lastName = etLastName.getText().toString();
            String firstName = etFirstName.getText().toString();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(getUID());
            //save:
            //in order to save -> user
            final String phoneNumber = etPhone.getText().toString();
            User newUser = new User(firstName, lastName, phoneNumber, getUID());
            usersRef.setValue(newUser);
        } else {
            Toast.makeText(getApplicationContext(), "נא למלא את כל הפרטים בבקשה", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFilled(Boolean b) {
        if (etPhone.getText() != null && etPhone.getText().toString().contains("05") && etPhone.getText().length() == 10) {
            if (etLastName.getText() != null && etFirstName.getText() != null)
                b = true;
        }
        if (etPhone.getText().length() > 10 || etPhone.getText().length() < 10) {
            etPhone.setError("מספר לא תקין");
            b = false;
        }
        if (etLastName.getText().toString().equals("")) {
            etLastName.setError("נא למלא");
            b = false;
        }
        if (etFirstName.getText().toString().equals("")) {
        etFirstName.setError("נא למלא");
            b = false;
        }

        return b;
    }

    private void writeBoolean(String s, String key, Boolean b) {
        //1) reference to the shared object (sharedPreferences)
        //singleton...? No new...?
        //allows us to Read data:
        SharedPreferences tk = getSharedPreferences(s/*xml file name...*/, Context.MODE_PRIVATE);
        //2) reference to the editor of the sharedPreferences
        //Writer
        SharedPreferences.Editor editor = tk.edit();
        //3) editor.put...(key, value).
        editor.putBoolean(key, b);
        editor.apply();//new Thread -> save();
    }


    private Boolean readSharedPrefs(String s, String key) {
        //1) get a reference to the shared preferences object. (singleton...Not new... getShared)
        SharedPreferences tk = getSharedPreferences(s/*xml file name...*/, Context.MODE_PRIVATE);
        //int val = obj.getIntValue()
        //3) return val
        return tk.getBoolean(key, true/*defaultValue*/);

    } //reads the Worker Name with Shared Preferences . :)

    private void writeUID(String b) {
        //1) reference to the shared object (sharedPreferences)
        //singleton...? No new...?
        //allows us to Read data:
        SharedPreferences tk = getSharedPreferences("UID"/*xml file name...*/, Context.MODE_PRIVATE);
        //2) reference to the editor of the sharedPreferences
        //Writer
        SharedPreferences.Editor editor = tk.edit();
        //3) editor.put...(key, value).
        editor.putString("uid", b);
        editor.apply();//new Thread -> save();
        uid=b;
    }

    private String getUID() {
        //1) get a reference to the shared preferences object. (singleton...Not new... getShared)
        SharedPreferences tk = getSharedPreferences("UID"/*xml file name...*/, Context.MODE_PRIVATE);
        //int val = obj.getIntValue()
        //3) return val
        uid = tk.getString("uid", null);
        return tk.getString("uid", null/*defaultValue*/);

    }

    static String getTheUID() {
        return uid;
    }

    private void signInAnony() {
        // [START signin_anonymously]
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeUID(user.getUid());
                            linkAccount();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }

    private void linkAccount() {
        // Get email and password from form
        String email = etPhone.getText().toString() + etPhone.getText().toString() + "@shai.com";
        String password = "123456";

        // Create EmailAuthCredential with email and password
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // Link the anonymous user to the email credential

        // [START link_credential]
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                        }

                    }
                });
    }


    private void userExistCheck(ShayBooleanListener listener) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");

        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
                assert u != null;
                final String uid = u.getUID();
                final String phoneNumber = u.getPhoneNumber();
                s = etPhone.getText().toString();
                if (phoneNumber.equals(s)) {
                    listener.result(true);
                    writeUID(uid);
                    Toast.makeText(LoginActivity.this, "שלום" + " " + u.getFirstName(), Toast.LENGTH_SHORT).show();
                    Log.e("USEREXISTS", "User exists");
                    return;
                } else listener.result(false);
                Log.e("USEREXISTS", "User is new");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
