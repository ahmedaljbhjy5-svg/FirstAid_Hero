package com.ahmed.firstaidhero1.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.CardItem;
import com.ahmed.firstaidhero1.model.CaseDetail;
import com.ahmed.firstaidhero1.model.Severity;
import com.ahmed.firstaidhero1.util.TextNormalizer;

import java.util.ArrayList;
import java.util.List;

public class EmergencyRepository {

    // الحرج أولاً، وداخل كل مستوى بترتيب إدخال الحالات (الأكثر شيوعاً أولاً)
    private static final String DEFAULT_ORDER =
            AppDatabase.COL_DANGER + " DESC, " + AppDatabase.COL_CASE_ID + " ASC";

    private final AppDatabase helper;

    public EmergencyRepository(Context context) {
        this.helper = AppDatabase.getInstance(context);
    }

    // بحث فوري: كل كلمة بالاستعلام لازم تكون موجودة (AND)، والنص فاضي = كل الحالات
    public List<CardItem> search(String rawQuery) {
        StringBuilder where = new StringBuilder();
        List<String> args = new ArrayList<>();
        for (String token : TextNormalizer.normalize(rawQuery).split(" ")) {
            if (token.isEmpty()) continue;
            if (where.length() > 0) where.append(" AND ");
            where.append(AppDatabase.COL_SEARCH).append(" LIKE ? ESCAPE '\\'");
            args.add("%" + TextNormalizer.escapeLike(token) + "%");
        }

        List<CardItem> results = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(
                AppDatabase.TABLE_EMERGENCIES,
                new String[]{AppDatabase.COL_CASE_ID, AppDatabase.COL_TITLE,
                        AppDatabase.COL_DANGER, AppDatabase.COL_SYMPTOMS},
                where.length() == 0 ? null : where.toString(),
                args.isEmpty() ? null : args.toArray(new String[0]),
                null, null, DEFAULT_ORDER)) {

            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_CASE_ID);
            int titleCol = c.getColumnIndexOrThrow(AppDatabase.COL_TITLE);
            int dangerCol = c.getColumnIndexOrThrow(AppDatabase.COL_DANGER);
            int symptomsCol = c.getColumnIndexOrThrow(AppDatabase.COL_SYMPTOMS);

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

    // حالة كاملة مع خطواتها مرتبة، أو null إذا الرقم غير موجود
    public CaseDetail getDetail(long caseId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] idArg = {String.valueOf(caseId)};

        try (Cursor c = db.query(
                AppDatabase.TABLE_EMERGENCIES,
                new String[]{AppDatabase.COL_TITLE, AppDatabase.COL_DANGER, AppDatabase.COL_SYMPTOMS},
                AppDatabase.COL_CASE_ID + " = ?", idArg, null, null, null)) {

            if (!c.moveToFirst()) return null;

            String title = c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_TITLE));
            Severity severity = Severity.fromLevel(c.getInt(c.getColumnIndexOrThrow(AppDatabase.COL_DANGER)));
            String symptoms = c.getString(c.getColumnIndexOrThrow(AppDatabase.COL_SYMPTOMS));

            List<String> steps = new ArrayList<>();
            try (Cursor s = db.query(
                    AppDatabase.TABLE_STEPS,
                    new String[]{AppDatabase.COL_INSTRUCTION},
                    AppDatabase.COL_CASE_ID + " = ?", idArg,
                    null, null, AppDatabase.COL_STEP_NUMBER + " ASC")) {
                int textCol = s.getColumnIndexOrThrow(AppDatabase.COL_INSTRUCTION);
                while (s.moveToNext()) {
                    steps.add(s.getString(textCol));
                }
            }
            return new CaseDetail(caseId, title, severity, symptoms, steps);
        }
    }
}