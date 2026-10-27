package com.ahmed.firstaidhero1.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmed.firstaidhero1.model.CaseStat;
import com.ahmed.firstaidhero1.model.DayStat;
import com.ahmed.firstaidhero1.model.MonthStat;
import com.ahmed.firstaidhero1.model.Severity;
import com.ahmed.firstaidhero1.model.SeverityStat;
import com.ahmed.firstaidhero1.util.Lang;

import java.util.ArrayList;
import java.util.List;

// كل الإحصائيات هون مبنية على الحالات المعالجة اللي سجّلناها (treated_cases)،
// مش على حالات الدليل. وخطورة واسم كل حالة بيجوا من JOIN مع emergencies
public class StatsRepository {

    private static final String T = AppDatabase.TABLE_TREATED;
    private static final String E = AppDatabase.TABLE_EMERGENCIES;
    private static final String JOIN = " FROM " + T + " JOIN " + E
            + " ON " + E + "." + AppDatabase.COL_CASE_ID + " = " + T + "." + AppDatabase.COL_CASE_ID;

    private final AppDatabase helper;

    public StatsRepository(Context context) {
        this.helper = AppDatabase.getInstance(context);
    }

    // عدد الحالات المعالجة بكل مستوى خطورة. الحرجة أولاً
    public List<SeverityStat> getSeverityStats() {
        // GROUP BY ما بيرجّع المجموعات الفاضية، فبنبدأ بأصفار ونعبّي اللي رجع
        int[] counts = new int[Severity.values().length + 1]; // الفهرس = رقم المستوى (1 إلى 3)

        String sql = "SELECT " + E + "." + AppDatabase.COL_DANGER + ", COUNT(*)" + JOIN
                + " GROUP BY " + E + "." + AppDatabase.COL_DANGER;
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

    // عدد الحالات المعالجة لكل حالة من الدليل (الأكثر تكراراً أولاً)، للحالات اللي إلها سجل فقط
    public List<CaseStat> getCaseStats() {
        String titleExpr = Lang.pick(E + "." + AppDatabase.COL_TITLE, E + "." + AppDatabase.COL_TITLE_EN);
        String sql = "SELECT " + titleExpr + " AS case_title, "
                + E + "." + AppDatabase.COL_DANGER + ", COUNT(*) AS n" + JOIN
                + " GROUP BY " + T + "." + AppDatabase.COL_CASE_ID
                + " ORDER BY n DESC, " + E + "." + AppDatabase.COL_DANGER + " DESC, "
                + E + "." + AppDatabase.COL_CASE_ID + " ASC";
        List<CaseStat> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(sql, null)) {
            while (c.moveToNext()) {
                result.add(new CaseStat(c.getString(0), Severity.fromLevel(c.getInt(1)), c.getInt(2)));
            }
        }
        return result;
    }

    // عدد الحالات المعالجة بكل شهر (yyyy-MM). الأحدث أولاً
    public List<MonthStat> getTreatedPerMonth() {
        String sql = "SELECT substr(" + AppDatabase.COL_TREATED_DATE + ", 1, 7) AS month, COUNT(*)"
                + " FROM " + T
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

    // عدد الحالات المعالجة بكل يوم (yyyy-MM-dd)، آخر limit أيام فيها تسجيل، الأحدث أولاً
    public List<DayStat> getTreatedPerDay(int limit) {
        // limit رقم int من عندنا (مش من المستخدم) فآمن نضمّنه بالنص
        String sql = "SELECT " + AppDatabase.COL_TREATED_DATE + ", COUNT(*)"
                + " FROM " + T
                + " GROUP BY " + AppDatabase.COL_TREATED_DATE
                + " ORDER BY " + AppDatabase.COL_TREATED_DATE + " DESC LIMIT " + Math.max(limit, 1);
        List<DayStat> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(sql, null)) {
            while (c.moveToNext()) {
                result.add(new DayStat(c.getString(0), c.getInt(1)));
            }
        }
        return result;
    }

    public int getTreatedTotal() {
        String sql = "SELECT COUNT(*) FROM " + T;
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(sql, null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }
}
