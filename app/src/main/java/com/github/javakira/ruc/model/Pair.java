package com.github.javakira.ruc.model;

public class Pair {
    int index;
    String name, by, place, type;

    public Pair(int index, String name, String by, String place, String type) {
        this.index = index;
        this.name = name;
        this.by = by;
        this.place = place;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getBy() {
        return by;
    }

    public String getPlace() {
        return place;
    }

    public String getType() {
        return type;
    }
}
