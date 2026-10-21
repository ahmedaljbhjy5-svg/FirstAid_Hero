package com.ahmed.firstaidhero1.model;

import com.ahmed.firstaidhero1.R;

public enum Severity {
    LOW(1, R.drawable.ic_severity_low, R.string.severity_low),
    MEDIUM(2, R.drawable.ic_severity_medium, R.string.severity_medium),
    HIGH(3, R.drawable.ic_severity_high, R.string.severity_high);

    public final int level;
    public final int iconRes;
    public final int labelRes;

    Severity(int level, int iconRes, int labelRes) {
        this.level = level;
        this.iconRes = iconRes;
        this.labelRes = labelRes;
    }

    // لقراءة القيمة من قاعدة البيانات لاحقاً (عمود INTEGER فيه 1 أو 2 أو 3)
    public static Severity fromLevel(int level) {
        for (Severity s : values()) {
            if (s.level == level) return s;
        }
        return HIGH; // قيمة تالفة أو ناقصة = نفترض الأخطر، مش الأخف
    }
}