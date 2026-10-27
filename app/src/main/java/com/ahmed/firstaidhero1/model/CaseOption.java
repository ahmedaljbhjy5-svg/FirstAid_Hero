package com.ahmed.firstaidhero1.model;

// عنصر بقائمة اختيار الحالة عند إضافة حالة معالجة
public class CaseOption {
    private final long id;
    private final String title;
    private final Severity severity;

    public CaseOption(long id, String title, Severity severity) {
        this.id = id;
        this.title = title;
        this.severity = severity;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public Severity getSeverity() { return severity; }

    // الـ ArrayAdapter بيعرض toString()
    @Override
    public String toString() { return title; }
}
