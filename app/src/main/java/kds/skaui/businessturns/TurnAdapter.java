package kds.skaui.businessturns;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TurnAdapter extends android.support.v7.widget.RecyclerView.Adapter<TurnAdapter.TurnViewHolder> {
    private ArrayList<Turn> turns;
    private Context context;
    //inflater -> takes an xml as a parameter and Creates a fully fledged android View from it.
    private LayoutInflater inflater;

    public TurnAdapter(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    TurnAdapter(ArrayList<Turn> turns, Context context) {
        this.turns = turns;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    //recycler.
    @Override
    public TurnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.turn_recycler, parent, false);

        return new TurnViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TurnViewHolder holder, int position) {
        final Turn turn = turns.get(position);
        String name = turn.getFirstName() +" " + turn.getLastName();
        holder.tvName.setText(name);
        String dateTime =turn.getDate() + " בשעה " + turn.getTime();
        holder.tvTurnTime.setText(dateTime);
        holder.tvServiceName.setText(turn.getTurnKind());

        holder.v.setOnClickListener(v -> Toast.makeText(context, "Turn of: " + turn.getFirstName(), Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getItemCount() {
        return turns.size();
    }

    //find view by id.
    static class TurnViewHolder extends RecyclerView.ViewHolder {
        //no encapsulation for efficiency:
        TextView tvName, tvServiceName, tvTurnTime;
        View v;

        //constructor:
        TurnViewHolder(View v) {
            super(v);
            this.v = v;
            tvName = v.findViewById(R.id.tvName);
            tvServiceName = v.findViewById(R.id.tvServiceName);
            tvTurnTime = v.findViewById(R.id.tvTurnTime);
        }
    }

}
