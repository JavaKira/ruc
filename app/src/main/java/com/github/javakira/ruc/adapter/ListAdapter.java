package com.github.javakira.ruc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.javakira.ruc.R;
import com.github.javakira.ruc.model.SpinnerItem;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private final Context context;
    private final List<SpinnerItem> itemList;

    public ListAdapter(Context context, List<SpinnerItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public SpinnerItem getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = LayoutInflater.from(context).inflate(R.layout.spinner_item, viewGroup, false);
        TextView textView = root.findViewById(R.id.spinner_title);
        textView.setText(itemList.get(i).getTitle());
        return root;
    }

    public List<SpinnerItem> getItemList() {
        return itemList;
    }
}
