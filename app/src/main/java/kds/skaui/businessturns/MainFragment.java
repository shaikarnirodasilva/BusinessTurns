package kds.skaui.businessturns;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    public static String FACEBOOK_URL = "https://www.facebook.com/ADIEL-HAIR-STYLE-1648157815433143/";
    public static String FACEBOOK_PAGE_ID = "1648157815433143";

    private SliderLayout sliderShow;
    private TextView tvNote, tvToday;
    FloatingActionButton fabFace;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        sliderShow = v.findViewById(R.id.slider);
        tvNote = v.findViewById(R.id.tvBody);
        tvToday = v.findViewById(R.id.tvToday);
        fabFace = v.findViewById(R.id.fabFace);


        fabFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getActivity());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });


        photoView();

        putNotification();
        return v;


    }

    void photoView() {
        HashMap<String, String> file_maps = new HashMap<String, String>();
        file_maps.put("ברוכים הבאים", "https://scontent.ftlv2-1.fna.fbcdn.net/v/t1.0-9/12308787_1648161078766150_5454610848493957403_n.jpg?_nc_cat=0&oh=12ecf6c65ee4b463d7ac29e689dc488a&oe=5B613195");
        file_maps.put("כל סוגי ההחלקות והתספורות", "https://scontent.ftlv2-1.fna.fbcdn.net/v/t1.0-9/16648986_1828726834042906_5225802525638991358_n.jpg?_nc_cat=0&oh=b09d18e9a1a8e4363196859c9519711e&oe=5B514B02");
        file_maps.put("מחירים","https://scontent.ftlv2-1.fna.fbcdn.net/v/t1.0-9/33234740_10212462695425331_3806902150378815488_n.jpg?_nc_cat=0&oh=169ff06f372ec68e23d6a106dce88bbe&oe=5B7998FD");

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getContext());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            sliderShow.addSlider(textSliderView);

        }
        sliderShow.setPresetTransformer(SliderLayout.Transformer.Fade);
        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderShow.setCustomAnimation(new DescriptionAnimation());
        sliderShow.setDuration(4000);


    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {//
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    private void putNotification() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Notifications");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    String key = dataSnapshot.getKey();
                    if (key != null) {
                        if (key.equals("body")) {
                            String value = dataSnapshot.getValue().toString();
                            tvNote.setText(value);

                        } else if (key.equals("date")) {
                            String value = dataSnapshot.getValue().toString();
                            String replace = value.replace("_", ".");
                            tvToday.setText(replace);
                        }
                    } else {
                        tvToday.setVisibility(View.GONE);
                        tvNote.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Problem in MainFragment!!!");
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


    //method to get the right URL to use in the intent

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }


}
