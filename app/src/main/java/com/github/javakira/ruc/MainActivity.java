package com.github.javakira.ruc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.github.javakira.ruc.adapter.PairAdapter;
import com.github.javakira.ruc.model.Pair;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView pairRecycler;
    private PairAdapter pairAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pairRecycler = findViewById(R.id.pairRecycler);

        List<Pair> pairList = new ArrayList<>();
        pairList.add(new Pair(0, "Литература", "Курасова Виктория Анатольевна", "ауд. В-202", "практические занятия"));
        pairList.add(new Pair(1, "Русский язык", "Курасова Виктория Анатольевна", "ауд. В-202", "практические занятия"));

        setPairRecycler(pairList);
    }

    private void setPairRecycler(List<Pair> pairList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        pairRecycler = findViewById(R.id.pairRecycler);
        pairRecycler.setLayoutManager(layoutManager);
        pairAdapter = new PairAdapter(this, pairList);
        pairRecycler.setAdapter(pairAdapter);
    }
}