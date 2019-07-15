package kds.skaui.businessturns;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by shaikarniro on 29.1.2018.
 */

public class FreeDateAdapter extends RecyclerView.Adapter<FreeDateAdapter.FreeDateViewHolder> {
    private ArrayList<String> freeDates;
    private Context context;
    //inflater -> takes an xml as a parameter and Creates a fully fledged android View from it.
    private LayoutInflater inflater;
    private final FreeTimeShowFragment mTime = new FreeTimeShowFragment();

    public FreeDateAdapter() {
    }

    FreeDateAdapter(ArrayList<String> freeDates, Context context) {
        this.freeDates = freeDates;
        this.context = context;
        this.inflater = LayoutInflater.from(context);


    }

    @Override
    public FreeDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.free_turn_item, parent, false);
        return new FreeDateViewHolder(v);

    }

    @Override
    public void onBindViewHolder(FreeDateViewHolder holder, int position) {
        String date = freeDates.get(position);
        holder.tvHour.setText(date);

        /*when clicking on a date it saved and going to choose time*/
        holder.tvHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.tvHour.getText().toString().equals("TextView") || !holder.tvHour.getText().toString().equals(null)) {
                    String text = holder.tvHour.getText().toString();
                    if (text.contains(" ")) {
                        String replace = text.replace(" ", "_");
                        writeDate(replace);
                    }
                    Toast.makeText(context, datePreferences(), Toast.LENGTH_SHORT).show();
                    //if there is a date , go to take a time.
                    //if there is not date set , so no date clicked.
                    if (datePreferences() != null) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
                        ft.replace(R.id.datesFree, new FreeTimeShowFragment()).commit();
                    } else Toast.makeText(context, "לא נבחר תאריך", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    @Override
    public int getItemCount() {
        return freeDates.size();
    }

    class FreeDateViewHolder extends RecyclerView.ViewHolder {
        //no encapsulation for efficiency:
        private TextView tvHour;
        View v;

        FreeDateViewHolder(View v) {
            super(v);
            this.v = v;
            tvHour = v.findViewById(R.id.tvHour);
        }

    }

    private void writeDate(String b) {
        //1) reference to the shared object (sharedPreferences)
        //singleton...? No new...?
        //allows us to Read data:
        SharedPreferences tk = MainActivity.getContextOfApplication().getSharedPreferences("Date"/*xml file name...*/, Context.MODE_PRIVATE);
        //2) reference to the editor of the sharedPreferences
        //Writer
        SharedPreferences.Editor editor = tk.edit();
        //3) editor.put...(key, value).
        editor.putString("date", b);
        editor.apply();//new Thread -> save();
    }

    private static String timePreferences() {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Time", Context.MODE_PRIVATE);
        return sp.getString("time", "");
    }

    private static String datePreferences() {
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = MainActivity.getContextOfApplication().getSharedPreferences("Date", Context.MODE_PRIVATE);
        return sp.getString("date", "");
    }
}
