package com.github.javakira.ruc.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.javakira.ruc.utils.FileIO;
import com.github.javakira.ruc.R;
import com.github.javakira.ruc.SettingsActivity;
import com.github.javakira.ruc.adapter.CardAdapter;
import com.github.javakira.ruc.databinding.FragmentScheduleBinding;
import com.github.javakira.ruc.model.Card;
import com.github.javakira.ruc.parser.RucParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private RecyclerView cardRecycler;
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

        cardRecycler = view.findViewById(R.id.cardRecycler);
        List<Card> cardList = new ArrayList<>();
        setCardRecycler(cardList);
    }

    private void setCardRecycler(List<Card> cardList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false) {

        };
        cardRecycler = view.findViewById(R.id.cardRecycler);
        cardRecycler.setLayoutManager(layoutManager);
        CardAdapter cardAdapter = new CardAdapter(view.getContext(), cardList);
        cardRecycler.setAdapter(cardAdapter);

        Properties properties = FileIO.loadProps(view.getContext());
        RucParser.useEmployeeCards(
                Objects.requireNonNull(properties.get("branch")).toString(),
                Objects.requireNonNull(properties.get("employee")).toString(),
                (Consumer<Card>) card -> {
                    cardList.add(card);
                    cardAdapter.notifyItemInserted(cardList.size());
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}