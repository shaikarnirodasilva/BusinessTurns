package kds.skaui.businessturns;

import android.support.annotation.NonNull;
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
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by shaikarniro on 14.1.2018.
 */

public class DayOfWeek {
    static int ihsa, loco;
    static boolean c, b;
    private static int openingMinute;
    private static int closingHour;
    private static int openingHour;
    private int closingMinute;
    private static HashMap<String, Boolean> isFreeMap = new HashMap<>();


    private static DateTime now = DateTime.now();

    private static int y = now.getYear();
    private static int m = now.getMonthOfYear();
    private static int d = now.getDayOfMonth();
    static DateTime calendarTime2 = new DateTime(y, m, d, openingHour, openingMinute);
    static DateTime calendarTime = new DateTime(y, m, d, openingHour, openingMinute);
    static LocalTime dateTime = new LocalTime(openingHour, openingMinute);
    private LocalTime today = new LocalTime();

    private boolean isFree = true;
    private Calendar cal = Calendar.getInstance();
    final static DateTimeFormatter df = DateTimeFormat.forPattern("HH:mm");
    final static DateTimeFormatter dff = DateTimeFormat.forPattern("yyyy_MM_dd");

    //Empty Constructor

    public DayOfWeek() {
        //CHECK IF YOU NEED TO INTIALIZE THE CALENDAR
        initializeCalendar();
        /*CHECK IF YOU NEED TO ADD ONE MORE DAY TO CALENDAR*/
        lompa();
    }

    //Constructor
    public DayOfWeek(int openingHour, int openingMinute, int closingHour, int closingMinute) {


        //How many half hours exist between the range.
        final int halfHoursCount = (closingHour - openingHour + 1) * 2;
        dateTime = dateTime.withMinuteOfHour(openingMinute);


        //Get one month database.
        DatabaseReference adielRef = FirebaseDatabase.getInstance().getReference("0528652868");
        DatabaseReference oRef = FirebaseDatabase.getInstance().getReference("0509596040");
        DatabaseReference cRef = adielRef.child("Calendar");
        DatabaseReference c2Ref = oRef.child("Calendar");

        //initialize the hashmap every day. and the hours+minutes everyday!!
        for (int j = 0; j < 30; j++) {
            HashMap<String, Boolean> isFreeMap1 = isFreeMap;


            //adds one day to the datetime!!
            calendarTime2 = calendarTime.plusDays(j + 1);

            //set the time of the date to the opening hour and minute that we wrote in MainActivity initialize.
            dateTime = dateTime.withHourOfDay(openingHour);
            dateTime = dateTime.withMinuteOfHour(openingMinute);

            //if the day today is saturday , add a day and write it to the Database
            String saturday = calendarTime2.dayOfWeek().getAsText();
            if (saturday.equals("יום שבת")) {
                //adds one day to the datetime so it wont be saturday anymore.
                calendarTime2 = calendarTime.plusDays(1);
                for (int k = 0; k < halfHoursCount; k++) {
                    if (dateTime.getHourOfDay() <= closingHour) {

                        dateTime = dateTime.plusMinutes(30);

                        isFreeMap1.put(dateTime.toString(df), true);
                    }
                }

            } else {
                /*Save it to the hashmap*/
                for (int i = 0; i < halfHoursCount; i++) {
                    if (dateTime.getHourOfDay() <= closingHour) {

                        dateTime = dateTime.plusMinutes(30);

                        isFreeMap1.put(dateTime.toString(df), true);

                    }
                }
            }
            //Checks that the HashMap is not nothing, and write it to database.
            if (isFreeMap1.size() != 0)

            {
                //save every EACH day in Firebase database.
                DatabaseReference calendarRef = cRef.child(calendarTime2.toString(dff) + "_" + hebrewDays(calendarTime2));
                DatabaseReference oCalendarRef = c2Ref.child(calendarTime2.toString(dff) + "_" + hebrewDays(calendarTime2));
                calendarRef.setValue(calendarTime2.dayOfWeek().getAsText());
                oCalendarRef.setValue(calendarTime2.dayOfWeek().getAsText());
                calendarRef.setValue(isFreeMap1);
                oCalendarRef.setValue(isFreeMap1);


                /* Checkings */
            } else

            {
                System.out.println("Problem in DOW");
                return;
            }
        }


//At the moment that that now time is bigger than closing hour --> delete the day from database.
//if today hour is bigger than the closing hour then the day has passed , delete the last day.
       /* if (today.getHourOfDay() >= closingHour) {
            LocalDate now = LocalDate.now();
            //delete the passed day!
            Task<Void> ref = cRef.child(now.toString(dff)).removeValue();
            Task<Void> ref2 = c2Ref.child(now.toString(dff)).removeValue();
            ref.addOnCompleteListener(task -> {
                System.out.println("Delete Success!!");

            });
            ref2.addOnCompleteListener(task -> {
                System.out.println("Delete Success!!");
            });
        } else System.out.println("השעה לא גדולה משעת הסיום או שלא קיים ערך במאגר");

*/
    }

//Getters && Setters


    public HashMap<String, Boolean> getIsFreeMap() {
        return isFreeMap;
    }


    //toString
    @Override
    public String toString() {
        return "DayOfWeek{" +
                "openingMinute=" + openingMinute +
                ", closingHour=" + closingHour +
                ", openingHour=" + openingHour +
                ", closingMinute=" + closingMinute +
                ", isFree=" + isFree +
                ", isFreeMap=" + isFreeMap +
                ", cal=" + cal +
                '}';
    }


    private static String hebrewDays(DateTime dTime) {
        String replace = "";
        String asText = dTime.dayOfWeek().getAsText();

        if (asText.equals("יום ראשון")) {
            replace = asText.replace("יום ראשון", "ראשון");
        }
        if (asText.equals("יום שני")) {
            replace = asText.replace("יום שני", "שני");
        }
        if (dTime.dayOfWeek().getAsText().equals("יום שלישי")) {
            replace = asText.replace("יום שלישי", "שלישי");
        }
        if (dTime.dayOfWeek().getAsText().equals("יום רביעי")) {
            replace = asText.replace("יום רביעי", "רביעי");
        }
        if (dTime.dayOfWeek().getAsText().equals("יום חמישי")) {
            replace = asText.replace("יום חמישי", "חמישי");
        }
        if (dTime.dayOfWeek().getAsText().equals("יום שישי")) {
            replace = asText.replace("יום שישי", "שישי");
        }
        return replace;
    }


    private void adiel30Days(ShayBooleanListener listener) {
        final DateTime dateTime = new DateTime();
        final DatabaseReference adielRef = FirebaseDatabase.getInstance().getReference("0528652868");
        final String lc = (dateTime.plusDays(30).toString(dff));
        final String s = lc + "_" + hebrewDays(dateTime.plusDays(30));
        adielRef.child("Calendar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> kok = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String key = snapshot.getKey();
                    kok.add(key);
                }
                int size = kok.size();
                String s1 = kok.get(size - 1);
                if (s.equals(s1)) {
                    //the day in 30 days is already exists - no need to update and add one more day - only tommorow.
                    listener.result(true);
                } else {
                    //Ding dong!!the 30 day database needs to be updated - add one more day!return false
                    //it stays false if there is no value equals to the date - it means we need to add one day.
                    listener.result(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void areThere30Days(ShayBooleanListener listener) {
        final DateTime dateTime = new DateTime();
        final DatabaseReference oRef = FirebaseDatabase.getInstance().getReference("0509596040");
        final String lc = (dateTime.plusDays(30).toString(dff));
        final String s = lc + "_" + hebrewDays(dateTime.plusDays(30));
        oRef.child("Calendar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> e = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String key = snapshot.getKey();
                    e.add(key);
                }
                int size = e.size();
                String s1 = e.get(size - 1);
                if (s.equals(s1)) {
                    //the day in 30 days is already exists - no need to update and add one more day - only tommorow.
                    listener.result(true);
                } else {
                    //Ding dong!!the 30 day database needs to be updated - add one more day!return false
                    //it stays false if there is no value equals to the date - it means we need to add one day.
                    listener.result(false);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void check25(ShayBooleanListener listener) {
        final DatabaseReference adielRef = FirebaseDatabase.getInstance().getReference("0528652868").child("Calendar");
        adielRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childrenCount = (int) dataSnapshot.getChildrenCount();
                if (childrenCount == 26) {
                    listener.result(true);

                } else if (childrenCount > 26) {
                    listener.result(true);
                } else {
                    listener.result(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void check25Other(ShayBooleanListener listen) {
        final DatabaseReference oRef = FirebaseDatabase.getInstance().getReference("0509596040").child("Calendar");
        oRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childrenCount = (int) dataSnapshot.getChildrenCount();
                if (childrenCount == 26) {
                    listen.result(true);
                } else if (childrenCount > 26) {
                    listen.result(true);
                } else listen.result(false);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //adds one day to database (adiel and more).
    private void hey() {
        LocalTime mdtme = new LocalTime(openingHour, openingMinute);
        final DatabaseReference adielRef = FirebaseDatabase.getInstance().getReference("0528652868").child("Calendar");
        final int halfHoursCount = (19 - 10 + 1) * 2;
        //if its false add one more day.
        HashMap<String, Boolean> isFreeMap2 = new HashMap<>();
        calendarTime2 = calendarTime.plusDays(30);
        mdtme = mdtme.withHourOfDay(10);
        mdtme = mdtme.withMinuteOfHour(0);
        final String seven = calendarTime2.dayOfWeek().getAsText();
        if (seven.equals("יום שבת")) {
            Log.e("DAY", "Saturday");

            //dont write saturday to the database .
        } else if (seven.equals("יום שישי")) {
            for (int i = 0; i < 10; i++) {
                if (mdtme.getHourOfDay() <= 15) {
                    mdtme = mdtme.plusMinutes(30);
                    isFreeMap2.put(mdtme.toString(df), true);
                }
            }
            Log.e("DAY", "Friday");
        } else {
            /*Save it to the hashmap*/
            for (int i = 0; i < halfHoursCount; i++) {
                if (mdtme.getHourOfDay() <= 19) {

                    mdtme = mdtme.plusMinutes(30);

                    isFreeMap2.put(mdtme.toString(df), true);

                }
            }
            Log.e("DAY", "Friday");
        }
        //Checks that the HashMap is not nothing, and write it to database.
        if (isFreeMap2.size() != 0) {
            //save every EACH day in Firebase database.
            DatabaseReference calendarRef = adielRef.child(calendarTime2.toString(dff) + "_" + hebrewDays(calendarTime2));
            calendarRef.setValue(calendarTime2.dayOfWeek().getAsText());
            calendarRef.setValue(isFreeMap2);
            //the one more day is added , then write true - its all ok.
            //if there is no need to
        }
    }

    private void bye() {
        LocalTime dtme = new LocalTime(openingHour, openingMinute);
        final DatabaseReference oRef = FirebaseDatabase.getInstance().getReference("0509596040").child("Calendar");
        final int halfHoursCount = (19 - 10 + 1) * 2;
        //if its false add one more day.
        HashMap<String, Boolean> isFreeMap2 = new HashMap<>();
        calendarTime2 = calendarTime.plusDays(30);
        dtme = dtme.withHourOfDay(10);
        dtme = dtme.withMinuteOfHour(0);
        final String seven = calendarTime2.dayOfWeek().getAsText();
        if (seven.equals("יום שבת")) {
            //dont write saturday to the database .
        } else {
            /*Save it to the hashmap*/
            for (int i = 0; i < halfHoursCount; i++) {
                if (dtme.getHourOfDay() <= 19) {

                    dtme = dtme.plusMinutes(30);

                    isFreeMap2.put(dtme.toString(df), true);

                }
            }
        }
        //Checks that the HashMap is not nothing, and write it to database.
        if (isFreeMap2.size() != 0) {
            //save every EACH day in Firebase database.
            DatabaseReference calendarRef = oRef.child(calendarTime2.toString(dff) + "_" + hebrewDays(calendarTime2));
            calendarRef.setValue(calendarTime2.dayOfWeek().getAsText());
            calendarRef.setValue(isFreeMap2);
            //the one more day is added , then write true - its all ok.
            //
        }
    }


    //Checks if there are any need to add day to calendar,  if not delete
    private void lompa() {
        DayOfWeek.this.check25(new ShayBooleanListener() {
            @Override
            public void result(boolean hasTurns) {
                if (!hasTurns) {
                    hey();
                } else
                    deleteLastDay();
            }
        });
        DayOfWeek.this.check25Other(new ShayBooleanListener() {
            @Override
            public void result(boolean hasTurns) {
                if (!hasTurns) {
                    bye();
                } else
                    deleteLastDay();
            }
        });

    }

    //Initialize only once the calendar .
    private void initializeCalendar() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("0509596040");
        if (db == null) {
            //  listener.result(false);
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

    //Deletes last day , and all the turns of the last day .IMPORTANT CODE!
    private void deleteLastDay() {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("0509596040").child("Calendar");
        final DatabaseReference dbb = FirebaseDatabase.getInstance().getReference("0528652868").child("Calendar");
        final DatabaseReference turns = FirebaseDatabase.getInstance().getReference("Turns");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childrenCount = (int) dataSnapshot.getChildrenCount();
                while (childrenCount > 26) {
                    String key = dataSnapshot.getKey();
                    Task<Void> voidTask = db.child(key).removeValue();
                    voidTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("childrenCount", "Success");
                        }
                    });
                }
                dbb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int childrenCount = (int) dataSnapshot.getChildrenCount();
                        while (childrenCount > 26) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();
                                dbb.child(key).removeValue();
                                Log.e("childrenCount", "Success");
                                return;

                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
