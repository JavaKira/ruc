package com.github.javakira.ruc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.github.javakira.ruc.adapter.PairAdapter;
import com.github.javakira.ruc.model.Pair;
import com.github.javakira.ruc.parser.RucParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
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

        Properties properties = FileIO.loadProps("config.txt", this);
        RucParser.usePairs(
                Objects.requireNonNull(properties.get("branch")).toString(),
                new Date(),
                Objects.requireNonNull(properties.get("employee")).toString(),
                (Function<Pair, Void>) pair -> {
            pairList.add(pair);
            pairAdapter.notifyItemInserted(pairList.size() - 1);
            return null;
        });
    }
}