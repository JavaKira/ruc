package com.github.javakira.ruc.model;

public class Kit implements SpinnerItem {
    public static final Kit empty = new Kit("--", "");
    private final String title;
    private final String value;

    public Kit(String title, String value) {
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
