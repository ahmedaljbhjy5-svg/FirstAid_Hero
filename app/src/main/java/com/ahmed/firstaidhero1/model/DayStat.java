package com.ahmed.firstaidhero1.model;

public class DayStat {
    private final String date; // yyyy-MM-dd
    private final int count;

    public DayStat(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() { return date; }
    public int getCount() { return count; }
}
