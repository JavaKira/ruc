package com.github.javakira.ruc.model;

public class Employee implements SpinnerItem {
    private final String title;
    private final String value;

    public Employee(String title, String value) {
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
