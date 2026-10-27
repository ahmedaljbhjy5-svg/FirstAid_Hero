package com.ahmed.firstaidhero1.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.CardItem;
import com.ahmed.firstaidhero1.model.CaseDetail;
import com.ahmed.firstaidhero1.model.CaseOption;
import com.ahmed.firstaidhero1.model.Severity;
import com.ahmed.firstaidhero1.util.Lang;
import com.ahmed.firstaidhero1.util.TextNormalizer;

import java.util.ArrayList;
import java.util.List;

public class EmergencyRepository {

    // الحرج أولاً، وداخل كل مستوى بترتيب إدخال الحالات (الأكثر شيوعاً أولاً)
    private static final String DEFAULT_ORDER =
            AppDatabase.COL_DANGER + " DESC, " + AppDatabase.COL_CASE_ID + " ASC";

    // أسماء مستعارة للأعمدة بلغة الجهاز (انظر Lang.pick)
    private static final String ALIAS_TITLE = "title_l";
    private static final String ALIAS_SYMPTOMS = "symptoms_l";
    private static final String ALIAS_STEP = "step_l";
    private static final String ALIAS_SOURCE = "source_l";

    private final AppDatabase helper;

    public EmergencyRepository(Context context) {
        this.helper = AppDatabase.getInstance(context);
    }

    private static String titleColumn() {
        return Lang.pick(AppDatabase.COL_TITLE, AppDatabase.COL_TITLE_EN) + " AS " + ALIAS_TITLE;
    }

    private static String symptomsColumn() {
        return Lang.pick(AppDatabase.COL_SYMPTOMS, AppDatabase.COL_SYMPTOMS_EN) + " AS " + ALIAS_SYMPTOMS;
    }

    // بحث فوري: كل كلمة بالاستعلام لازم تكون موجودة (AND) بالنص العربي أو الإنجليزي،
    // فالمستخدم بيلاقي الحالة مهما كانت لغة واجهته. والنص فاضي = كل الحالات
    public List<CardItem> search(String rawQuery) {
        StringBuilder where = new StringBuilder();
        List<String> args = new ArrayList<>();
        for (String token : TextNormalizer.normalize(rawQuery).split(" ")) {
            if (token.isEmpty()) continue;
            if (where.length() > 0) where.append(" AND ");
            where.append("(")
                    .append(AppDatabase.COL_SEARCH).append(" LIKE ? ESCAPE '\\' OR ")
                    .append(AppDatabase.COL_SEARCH_EN).append(" LIKE ? ESCAPE '\\')");
            String like = "%" + TextNormalizer.escapeLike(token) + "%";
            args.add(like);
            args.add(like);
        }

        List<CardItem> results = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(
                AppDatabase.TABLE_EMERGENCIES,
                new String[]{AppDatabase.COL_CASE_ID, titleColumn(),
                        AppDatabase.COL_DANGER, symptomsColumn()},
                where.length() == 0 ? null : where.toString(),
                args.isEmpty() ? null : args.toArray(new String[0]),
                null, null, DEFAULT_ORDER)) {

            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_CASE_ID);
            int titleCol = c.getColumnIndexOrThrow(ALIAS_TITLE);
            int dangerCol = c.getColumnIndexOrThrow(AppDatabase.COL_DANGER);
            int symptomsCol = c.getColumnIndexOrThrow(ALIAS_SYMPTOMS);

            while (c.moveToNext()) {
                results.add(new CardItem(
                        c.getLong(idCol),
                        c.getString(titleCol),
                        c.getString(symptomsCol),
                        Severity.fromLevel(c.getInt(dangerCol)),
                        R.mipmap.ic_launcher)); // مؤقت لحد ما نجهّز الصور
            }
        }
        return results;
    }

    // كل الحالات (اسم + خطورة) لقائمة الاختيار بشاشة إضافة حالة معالجة، مرتبة أبجدياً
    public List<CaseOption> getCaseOptions() {
        List<CaseOption> options = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(
                AppDatabase.TABLE_EMERGENCIES,
                new String[]{AppDatabase.COL_CASE_ID, titleColumn(), AppDatabase.COL_DANGER},
                null, null, null, null,
                ALIAS_TITLE + " COLLATE NOCASE ASC")) {

            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_CASE_ID);
            int titleCol = c.getColumnIndexOrThrow(ALIAS_TITLE);
            int dangerCol = c.getColumnIndexOrThrow(AppDatabase.COL_DANGER);
            while (c.moveToNext()) {
                options.add(new CaseOption(
                        c.getLong(idCol),
                        c.getString(titleCol),
                        Severity.fromLevel(c.getInt(dangerCol))));
            }
        }
        return options;
    }

    // حالة كاملة مع خطواتها ومصدرها مرتبة، أو null إذا الرقم غير موجود
    public CaseDetail getDetail(long caseId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] idArg = {String.valueOf(caseId)};

        try (Cursor c = db.query(
                AppDatabase.TABLE_EMERGENCIES,
                new String[]{titleColumn(), AppDatabase.COL_DANGER, symptomsColumn(),
                        Lang.pick(AppDatabase.COL_SOURCE, AppDatabase.COL_SOURCE_EN)
                                + " AS " + ALIAS_SOURCE},
                AppDatabase.COL_CASE_ID + " = ?", idArg, null, null, null)) {

            if (!c.moveToFirst()) return null;

            String title = c.getString(c.getColumnIndexOrThrow(ALIAS_TITLE));
            Severity severity = Severity.fromLevel(c.getInt(c.getColumnIndexOrThrow(AppDatabase.COL_DANGER)));
            String symptoms = c.getString(c.getColumnIndexOrThrow(ALIAS_SYMPTOMS));
            String source = c.getString(c.getColumnIndexOrThrow(ALIAS_SOURCE));

            List<String> steps = new ArrayList<>();
            try (Cursor s = db.query(
                    AppDatabase.TABLE_STEPS,
                    new String[]{Lang.pick(AppDatabase.COL_INSTRUCTION, AppDatabase.COL_INSTRUCTION_EN)
                            + " AS " + ALIAS_STEP},
                    AppDatabase.COL_CASE_ID + " = ?", idArg,
                    null, null, AppDatabase.COL_STEP_NUMBER + " ASC")) {
                int textCol = s.getColumnIndexOrThrow(ALIAS_STEP);
                while (s.moveToNext()) {
                    steps.add(s.getString(textCol));
                }
            }
            return new CaseDetail(caseId, title, severity, symptoms, steps, source);
        }
    }
}
