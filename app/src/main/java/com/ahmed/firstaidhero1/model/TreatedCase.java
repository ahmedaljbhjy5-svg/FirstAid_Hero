package com.ahmed.firstaidhero1.model;

import androidx.annotation.Nullable;

public class TreatedCase {
    private final long id;
    private final long caseId;
    private final String caseTitle;
    private final Severity severity;
    private final String description;
    private final String procedures;
    private final String date;
    @Nullable private final String photoPath;

    public TreatedCase(long id, long caseId, String caseTitle, Severity severity,
                       String description, String procedures,
                       String date, @Nullable String photoPath) {
        this.id = id;
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.severity = severity;
        this.description = description;
        this.procedures = procedures;
        this.date = date;
        this.photoPath = photoPath;
    }

    public long getId() { return id; }
    public long getCaseId() { return caseId; }
    public String getCaseTitle() { return caseTitle; }
    public Severity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getProcedures() { return procedures; }
    public String getDate() { return date; }
    @Nullable public String getPhotoPath() { return photoPath; }
}
