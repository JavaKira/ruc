package com.github.javakira.ruc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.javakira.ruc.R;
import com.github.javakira.ruc.model.Pair;

import java.util.List;

public class PairAdapter extends RecyclerView.Adapter<PairAdapter.PairViewHolder> {
    final Context context;
    final List<Pair> pairList;

    public PairAdapter(Context context, List<Pair> pairList) {
        this.context = context;
        this.pairList = pairList;
    }

    @NonNull
    @Override
    public PairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new PairViewHolder(categoryItems);
    }

    @Override
    public int getItemCount() {
        return pairList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PairViewHolder holder, int position) {
        holder.visit(pairList.get(position));
    }

    static class PairViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, by, place, type;
        View view;

        public PairViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            name = itemView.findViewById(R.id.item_name);
            time = itemView.findViewById(R.id.item_time);
            by = itemView.findViewById(R.id.item_by);
            place = itemView.findViewById(R.id.item_place);
            type = itemView.findViewById(R.id.item_type);
        }

        public void visit(Pair pair) {
            name.setText((pair.getIndex() + 1) + ". " + pair.getName());
            by.setText(pair.getBy());
            time.setText(view.getContext().getResources().getString(R.string.p1 + pair.getIndex()));
            place.setText(pair.getPlace());
            type.setText(pair.getType());
        }
    }
}
