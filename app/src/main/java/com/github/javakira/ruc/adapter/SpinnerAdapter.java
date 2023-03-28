package com.github.javakira.ruc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.javakira.ruc.R;
import com.github.javakira.ruc.model.SpinnerItem;

import java.util.List;
import java.util.function.Function;

public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.SpinnerViewHolder> {
    private final Context context;
    private final List<SpinnerItem> items;
    private final Function<SpinnerItem, Void> onClick;

    public SpinnerAdapter(Context context, List<SpinnerItem> items, Function<SpinnerItem, Void> onClick) {
        this.context = context;
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public SpinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        return new SpinnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpinnerViewHolder holder, int position) {
        SpinnerItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.itemView.setOnClickListener(view -> {
            onClick.apply(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SpinnerViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public SpinnerViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.spinner_title);
        }
    }
}
