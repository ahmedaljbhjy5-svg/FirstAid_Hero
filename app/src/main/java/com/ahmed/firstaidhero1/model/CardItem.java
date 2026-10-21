package com.ahmed.firstaidhero1.model;

public class CardItem {
    private final String title;
    private final String description;
    private final Severity severity;
    private final int imageRes;

    public CardItem(String title, String description, Severity severity, int imageRes) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.imageRes = imageRes;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Severity getSeverity() { return severity; }
    public int getImageRes() { return imageRes; }
}