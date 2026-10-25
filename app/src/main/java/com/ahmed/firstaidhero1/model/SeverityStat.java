package com.ahmed.firstaidhero1.model;

public class SeverityStat {
    private final Severity severity;
    private final int count;

    public SeverityStat(Severity severity, int count) {
        this.severity = severity;
        this.count = count;
    }

    public Severity getSeverity() { return severity; }
    public int getCount() { return count; }
}