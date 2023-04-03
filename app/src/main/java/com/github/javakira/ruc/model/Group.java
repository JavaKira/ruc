package com.github.javakira.ruc.model;

public class Group implements SpinnerItem {
    public static final Group empty = new Group("--", "");
    private final String title;
    private final String value;

    public Group(String title, String value) {
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
