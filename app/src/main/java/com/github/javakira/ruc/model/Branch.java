package com.github.javakira.ruc.model;

public class Branch implements SpinnerItem {
    public static final Branch empty = new Branch("--", "");
    private final String title;
    private final String value;

    public Branch(String title, String value) {
        this.title = title;
        this.value = value;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getValue() {
        return value;
    }
}
