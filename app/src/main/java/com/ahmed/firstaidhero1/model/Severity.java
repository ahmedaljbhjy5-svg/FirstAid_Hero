package com.ahmed.firstaidhero1.model;

import com.ahmed.firstaidhero1.R;

public enum Severity {
    LOW(1, R.drawable.ic_severity_low, R.string.severity_low,
            R.string.severity_name_low, R.color.urgency_mild),
    MEDIUM(2, R.drawable.ic_severity_medium, R.string.severity_medium,
            R.string.severity_name_medium, R.color.urgency_serious),
    HIGH(3, R.drawable.ic_severity_high, R.string.severity_high,
            R.string.severity_name_high, R.color.urgency_critical);

    public final int level;
    public final int iconRes;
    public final int labelRes;
    public final int nameRes;
    public final int colorRes;

    Severity(int level, int iconRes, int labelRes, int nameRes, int colorRes) {
        this.level = level;
        this.iconRes = iconRes;
        this.labelRes = labelRes;
        this.nameRes = nameRes;
        this.colorRes = colorRes;
    }


    public static Severity fromLevel(int level) {
        for (Severity s : values()) {
            if (s.level == level) return s;
        }
        return HIGH;
    }
}