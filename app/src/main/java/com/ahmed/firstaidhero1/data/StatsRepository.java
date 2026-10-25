package com.ahmed.firstaidhero1.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmed.firstaidhero1.model.MonthStat;
import com.ahmed.firstaidhero1.model.Severity;
import com.ahmed.firstaidhero1.model.SeverityStat;

import java.util.ArrayList;
import java.util.List;

public class StatsRepository {

    private final AppDatabase helper;

    public StatsRepository(Context context) {
        this.helper = AppDatabase.getInstance(context);
    }

    // عدد الحالات بكل مستوى خطورة. الحرجة أولاً
    public List<SeverityStat> getSeverityStats() {
        // GROUP BY ما بيرجّع المجموعات الفاضية، فبنبدأ بأصفار ونعبّي اللي رجع
        int[] counts = new int[Severity.values().length + 1]; // الفهرس = رقم المستوى (1 إلى 3)

        String sql = "SELECT " + AppDatabase.COL_DANGER + ", COUNT(*) FROM "
                + AppDatabase.TABLE_EMERGENCIES
                + " GROUP BY " + AppDatabase.COL_DANGER;
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(sql, null)) {
            while (c.moveToNext()) {
                int level = c.getInt(0);
                if (level >= 1 && level < counts.length) counts[level] = c.getInt(1);
            }
        }

        List<SeverityStat> result = new ArrayList<>();
        result.add(new SeverityStat(Severity.HIGH, counts[Severity.HIGH.level]));
        result.add(new SeverityStat(Severity.MEDIUM, counts[Severity.MEDIUM.level]));
        result.add(new SeverityStat(Severity.LOW, counts[Severity.LOW.level]));
        return result;
    }

    // عدد الحالات المعالجة بكل شهر (yyyy-MM). الأحدث أولاً
    public List<MonthStat> getTreatedPerMonth() {
        String sql = "SELECT substr(" + AppDatabase.COL_TREATED_DATE + ", 1, 7) AS month, COUNT(*)"
                + " FROM " + AppDatabase.TABLE_TREATED
                + " GROUP BY month ORDER BY month DESC";
        List<MonthStat> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(sql, null)) {
            while (c.moveToNext()) {
                result.add(new MonthStat(c.getString(0), c.getInt(1)));
            }
        }
        return result;
    }

    public int getTreatedTotal() {
        String sql = "SELECT COUNT(*) FROM " + AppDatabase.TABLE_TREATED;
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(sql, null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }
}