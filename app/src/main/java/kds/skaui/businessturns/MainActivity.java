package kds.skaui.businessturns;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "New Token";
    public static Turn turn = new Turn();
    public static Context contextOfApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Checks if the user is not null . if it is =--> go to sign up .
        setTitle("מספרת עדיאל כחלון");
        writeShared(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "להתקשר לייעוץ?", Snackbar.LENGTH_LONG)
                .setAction("התקשר", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(("tel:" + "0528652868")));
                        if (intent.resolveActivity(getPackageManager()) != null) {

                            startActivity(intent);
                        }
                    }
                }).show());


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        contextOfApplication = getApplicationContext();
        String token = FirebaseInstanceId.getInstance().getToken();
        changeSupportFrame(new MainFragment());
        if (LoginActivity.getTheUID() != null) {
            return;
        } else {
            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.on_us) {
            changeSupportFrame(new MainFragment());

        } else if (id == R.id.get_turn) {
            changeSupportFrame(new WorkerFragment());

        } else if (id == R.id.remove_turn) {
            changeSupportFrame(new CancelFragment());
        } else if (id == R.id.sign_out) {
            signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //get Context of App.
    public static Context getContextOfApplication() {
        return contextOfApplication;
    }


    //Changes the fragment
    private void changeSupportFrame(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        ft.replace(R.id.content_main, fragment).commit();

    }

    //Writes Shared Preferences If this is a new User .
    private void writeShared(Boolean b) {
        //1) reference to the shared object (sharedPreferences)
        //singleton...? No new...?
        //allows us to Read data:
        SharedPreferences tk = getSharedPreferences("Boolean"/*xml file name...*/, Context.MODE_PRIVATE);
        //2) reference to the editor of the sharedPreferences
        //Writer
        SharedPreferences.Editor editor = tk.edit();
        //3) editor.put...(key, value).
        editor.putBoolean("boolean", b);
        editor.apply();//new Thread -> save();
    }

    //Checks if the user has firstname and last name if not, register again.
    private void checkUser() {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(LoginActivity.getTheUID());
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
                String firstName = u.getFirstName();
                String lastName = u.getLastName();
                if (firstName.equals("") || lastName.equals("")) {
                    writeUID(null);
                }

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

    //Writes the uid into Shared Preferences.
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
    }

    //Sign out to Sign Up.
    private void signOut() {
        AuthUI.getInstance().signOut(this);
        writeUID(null);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
