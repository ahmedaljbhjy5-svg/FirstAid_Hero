package com.ahmed.firstaidhero1.model;

public class MonthStat {
    private final String month; // yyyy-MM
    private final int count;

    public MonthStat(String month, int count) {
        this.month = month;
        this.count = count;
    }

    public String getMonth() { return month; }
    public int getCount() { return count; }
}