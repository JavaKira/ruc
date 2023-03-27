package com.github.javakira.ruc.model;

public class Pair {
    int index;
    String name, by, place, type, group;

    public Pair(int index, String name, String by, String place, String type, String group) {
        this.index = index;
        this.name = name;
        this.by = by;
        this.place = place;
        this.type = type;
        this.group = group;
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

    public String getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", by='" + by + '\'' +
                ", place='" + place + '\'' +
                ", type='" + type + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
