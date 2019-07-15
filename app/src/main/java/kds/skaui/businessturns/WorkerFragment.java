package kds.skaui.businessturns;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkerFragment extends Fragment {
    Worker adiel = new Worker("עדיאל", "אומן התספורות", "0528652868");
    Worker yohai = new Worker("יוחאי ", "ספר מתמחה", "0509596040");
    TextView tvWorker, tvWorker2;

    public WorkerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_worker, container, false);

        tvWorker = v.findViewById(R.id.tvWorker);
        tvWorker2 = v.findViewById(R.id.tvWorker2);

        tvWorker.setText(String.format("%s \n %s", adiel.getName(), adiel.getDescription()));
        tvWorker2.setText(String.format("%s \n %s", yohai.getName(), yohai.getDescription()));

        tvWorker.setOnClickListener(v12 -> {
            WorkerFragment.this.changeFragment(new GenderFragment());
            final String workerName = adiel.getPhoneNumber();
            //1) reference to the shared object (sharedPreferences)
            //singleton...? No new...?
            //allows us to Read data:
            SharedPreferences tk = getActivity().getSharedPreferences("WorkerName"/*xml file name...*/, Context.MODE_PRIVATE);
            //2) reference to the editor of the sharedPreferences
            //Writer
            SharedPreferences.Editor editor = tk.edit();
            //3) editor.put...(key, value).
            editor.putString("workerFirstName", workerName);
            editor.apply();//new Thread -> save();
        });
        tvWorker2.setOnClickListener(v1 -> {
            WorkerFragment.this.changeFragment(new GenderFragment());
            final String workerName = yohai.getPhoneNumber();
            //1) reference to the shared object (sharedPreferences)
            //singleton...? No new...?
            //allows us to Read data:
            SharedPreferences tk = getActivity().getSharedPreferences("WorkerName"/*xml file name...*/, Context.MODE_PRIVATE);
            //2) reference to the editor of the sharedPreferences
            //Writer
            SharedPreferences.Editor editor = tk.edit();
            //3) editor.put...(key, value).
            editor.putString("workerFirstName", workerName);
            editor.apply();//new Thread -> save();
        });


        return v;
    }

    //Change frame.
    void changeFragment(Fragment fragment) {
        getChildFragmentManager().
                beginTransaction().setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left).
                replace(R.id.workerFragment, fragment)
                .commit();
    }



}
