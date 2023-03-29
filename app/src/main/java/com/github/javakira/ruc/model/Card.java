package com.github.javakira.ruc.model;

import java.util.Date;
import java.util.List;

public class Card {
    private final Date date;
    private final List<Pair> pairList;

    public Card(Date date, List<Pair> pairList) {
        this.date = date;
        this.pairList = pairList;
    }

    public Date getDate() {
        return date;
    }

    public List<Pair> getPairList() {
        return pairList;
    }
}
