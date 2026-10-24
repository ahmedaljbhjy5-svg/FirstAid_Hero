package com.ahmed.firstaidhero1.model;

import java.util.List;

public class CaseDetail {
    private final long id;
    private final String title;
    private final Severity severity;
    private final String symptoms;
    private final List<String> steps;

    public CaseDetail(long id, String title, Severity severity, String symptoms, List<String> steps) {
        this.id = id;
        this.title = title;
        this.severity = severity;
        this.symptoms = symptoms;
        this.steps = steps;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public Severity getSeverity() { return severity; }
    public String getSymptoms() { return symptoms; }
    public List<String> getSteps() { return steps; }
}