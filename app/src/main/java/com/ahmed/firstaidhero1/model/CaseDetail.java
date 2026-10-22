package com.ahmed.firstaidhero1.model;

import java.util.List;

public class CaseDetail {
    private final int id;
    private final String title;
    private final String description;
    private final Severity severity;
    private final String helpWhen;
    private final List<String> steps;
    private final List<String> donts;

    public CaseDetail(int id, String title, String description, Severity severity,
                      String helpWhen, List<String> steps, List<String> donts) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.helpWhen = helpWhen;
        this.steps = steps;
        this.donts = donts;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Severity getSeverity() { return severity; }
    public String getHelpWhen() { return helpWhen; }
    public List<String> getSteps() { return steps; }
    public List<String> getDonts() { return donts; }
}