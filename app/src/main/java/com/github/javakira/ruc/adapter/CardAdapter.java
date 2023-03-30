package com.github.javakira.ruc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.javakira.ruc.R;
import com.github.javakira.ruc.model.Card;
import com.github.javakira.ruc.model.Pair;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    final Context context;
    final List<Card> cardList;

    public CardAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(categoryItems);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.visit(cardList.get(position));
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        View view;
        RecyclerView pairRecycler;
        TextView title;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            pairRecycler = itemView.findViewById(R.id.pairRecycler);
            title = itemView.findViewById(R.id.card_title);
        }

        public void visit(Card card) {
            title.setText(card.getDate().toString());
            setPairRecycler(card.getPairList());
        }

        private void setPairRecycler(List<Pair> pairList) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
            pairRecycler = view.findViewById(R.id.pairRecycler);
            pairRecycler.setLayoutManager(layoutManager);
            PairAdapter pairAdapter = new PairAdapter(view.getContext(), pairList);
            pairRecycler.setAdapter(pairAdapter);
        }
    }
}
