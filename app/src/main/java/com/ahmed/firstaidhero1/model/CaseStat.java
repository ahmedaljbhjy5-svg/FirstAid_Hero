package com.ahmed.firstaidhero1.model;

// عدد مرات معالجة حالة معينة من الدليل
public class CaseStat {
    private final String title;
    private final Severity severity;
    private final int count;

    public CaseStat(String title, Severity severity, int count) {
        this.title = title;
        this.severity = severity;
        this.count = count;
    }

    public String getTitle() { return title; }
    public Severity getSeverity() { return severity; }
    public int getCount() { return count; }
}
