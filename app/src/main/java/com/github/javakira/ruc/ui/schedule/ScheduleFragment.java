package com.github.javakira.ruc.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.javakira.ruc.FileIO;
import com.github.javakira.ruc.R;
import com.github.javakira.ruc.SettingsActivity;
import com.github.javakira.ruc.adapter.PairAdapter;
import com.github.javakira.ruc.databinding.FragmentScheduleBinding;
import com.github.javakira.ruc.model.Card;
import com.github.javakira.ruc.model.Pair;
import com.github.javakira.ruc.parser.RucParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private RecyclerView pairRecycler;
    private PairAdapter pairAdapter;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        if (!FileIO.isExist("config.txt", requireContext())) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        }

        pairRecycler = view.findViewById(R.id.pairRecycler);
        List<Pair> pairList = new ArrayList<>();
        setPairRecycler(pairList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setPairRecycler(List<Pair> pairList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        pairRecycler = view.findViewById(R.id.pairRecycler);
        pairRecycler.setLayoutManager(layoutManager);
        pairAdapter = new PairAdapter(getContext(), pairList);
        pairRecycler.setAdapter(pairAdapter);

        Properties properties = FileIO.loadProps("config.txt", getContext());
        RucParser.useCards(
                Objects.requireNonNull(properties.get("branch")).toString(),
                Objects.requireNonNull(properties.get("employee")).toString(),
                (Consumer<Card>) card -> {
                    Date date = new Date();
                    if (card.getDate().compareTo(new Date(date.getYear(), date.getMonth(), date.getDate() + 1)) == 0) {
                        pairList.addAll(card.getPairList());
                        pairAdapter.notifyItemInserted(pairList.size() - 1);
                    }
                });
    }
}