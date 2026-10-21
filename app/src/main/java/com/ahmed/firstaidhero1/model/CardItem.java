package com.ahmed.firstaidhero1.model;

public class CardItem {
    private final String title;
    private final String subtitle;
    private final int imageRes;

    public CardItem(String title, String subtitle, int imageRes) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageRes = imageRes;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getImageRes() { return imageRes; }
}