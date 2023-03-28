package com.github.javakira.ruc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.javakira.ruc.adapter.SpinnerAdapter;
import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.SpinnerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SpinnerFacade {
    private RecyclerView recycler;
    private SpinnerAdapter spinnerAdapter;
    private TextView mainTitle;
    private CardView main;
    private SpinnerItem item;
    private Context context;
    private List<SpinnerItem> items;

    public SpinnerFacade(RecyclerView recycler, TextView mainTitle, CardView main, SpinnerItem item, Context context, List<SpinnerItem> items) {
        this.recycler = recycler;
        this.mainTitle = mainTitle;
        this.main = main;
        this.item = item;
        this.context = context;
        this.items = items;

        main.setOnClickListener(view -> {
            if (recycler.getVisibility() == View.VISIBLE)
                recycler.setVisibility(View.GONE);
            else
                recycler.setVisibility(View.VISIBLE);
        });

        updateBranchItem(item);
        setRecycler();
    }

    private void setRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        spinnerAdapter = new SpinnerAdapter(context, items, (Function<SpinnerItem, Void>) item -> {
            updateBranchItem(item);
            recycler.setVisibility(View.GONE);
            return null;
        });

        recycler.setAdapter(spinnerAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void notifyChange() {
        spinnerAdapter.notifyDataSetChanged();
    }

    private void updateBranchItem(SpinnerItem item) {
        this.item = item;
        mainTitle.setText(item.getTitle());
    }
}
