package com.ahmed.firstaidhero1.model;

import androidx.annotation.Nullable;

public class TreatedCase {
    private final long id;
    private final String description;
    private final String procedures;
    private final String date;          // yyyy-MM-dd
    @Nullable private final String photoPath; // مسار نسبي داخل files/، أو null

    public TreatedCase(long id, String description, String procedures,
                       String date, @Nullable String photoPath) {
        this.id = id;
        this.description = description;
        this.procedures = procedures;
        this.date = date;
        this.photoPath = photoPath;
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public String getProcedures() { return procedures; }
    public String getDate() { return date; }
    @Nullable public String getPhotoPath() { return photoPath; }
}