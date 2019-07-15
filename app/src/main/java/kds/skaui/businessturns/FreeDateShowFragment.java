package kds.skaui.businessturns;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FreeDateShowFragment extends Fragment implements FreeDateDataSource.OnFreeDateArrivedListener {


    public FreeDateShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_free_date_show, container, false);

        FreeDateDataSource.getFreeTurns(this);


        return v;
    }
    @Override
    public void onFreeDateArrived(@Nullable ArrayList<String> freeDates, @Nullable Exception e) {
        getActivity().runOnUiThread(() -> {
            System.out.println();
            if (freeDates != null) {
                updateUI(freeDates);

            } else if (e != null) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
    void updateUI(ArrayList<String> freeDates) {
        //1) find the recycler by id.
        View v = getView();
        assert v != null;
        RecyclerView rvFreeDates = v.findViewById(R.id.rvFreeDates);

        //the adapter takes movies and provides Views for the movies.
        //2) MoviesAdapter adapter = new Movies adapter (movies, context)
        FreeDateAdapter adapter = new FreeDateAdapter(freeDates, getActivity());

        //3) recycler -> take the adapter.
        rvFreeDates.setAdapter(adapter);

        //4)
        rvFreeDates.setLayoutManager(new LinearLayoutManager(null));
    }

}
