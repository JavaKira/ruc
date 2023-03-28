package com.github.javakira.ruc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.javakira.ruc.adapter.PairAdapter;
import com.github.javakira.ruc.model.Pair;
import com.github.javakira.ruc.parser.RucParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {
    private RecyclerView pairRecycler;
    private PairAdapter pairAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!FileIO.isExist("config.txt", this)) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        pairRecycler = findViewById(R.id.pairRecycler);
        List<Pair> pairList = new ArrayList<>();
        setPairRecycler(pairList);
    }

    private void setPairRecycler(List<Pair> pairList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        pairRecycler = findViewById(R.id.pairRecycler);
        pairRecycler.setLayoutManager(layoutManager);
        pairAdapter = new PairAdapter(this, pairList);
        pairRecycler.setAdapter(pairAdapter);

        RucParser.usePairs(
                "4935b400-0858-11e0-8be3-005056bd3ce5",
                new Date(),
                "3c14c52e-132f-11ed-b15e-3cecef02455b",
                (Function<Pair, Void>) pair -> {
            pairList.add(pair);
            pairAdapter.notifyItemInserted(pairList.size() - 1);
            return null;
        });
    }
}