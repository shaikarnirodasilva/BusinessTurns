package kds.skaui.businessturns;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenderFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gender, container, false);

        final Button btnMale = v.findViewById(R.id.btnMale);
        final Button btnFemale = v.findViewById(R.id.btnFemale);

        btnMale.setOnClickListener(v12 -> {
            final String s = btnMale.getText().toString();
            SharedPreferences tk = getActivity().getSharedPreferences("gender"/*xml file name...*/, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = tk.edit();
            editor.putString("genderKind", s);
            editor.apply();
            changeFrame(new ServicesFragment());
            btnMale.setVisibility(View.INVISIBLE);
            btnFemale.setVisibility(View.INVISIBLE);
        });
        btnFemale.setOnClickListener(v1 -> {
            final String s = btnFemale.getText().toString();
            SharedPreferences tk = getActivity().getSharedPreferences("gender"/*xml file name...*/, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = tk.edit();
            editor.putString("genderKind", s);
            editor.apply();
            changeFrame(new ServicesFragment());
            btnFemale.setVisibility(View.INVISIBLE);
            btnMale.setVisibility(View.INVISIBLE);
        });
        return v;
    }

    void changeFrame(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        ft.replace(R.id.genderFrag, fragment).commit();
    }
}
