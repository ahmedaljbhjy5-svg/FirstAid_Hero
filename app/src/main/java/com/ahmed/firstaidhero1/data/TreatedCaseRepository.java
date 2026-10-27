package com.ahmed.firstaidhero1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.annotation.Nullable;

import com.ahmed.firstaidhero1.model.Severity;
import com.ahmed.firstaidhero1.model.TreatedCase;
import com.ahmed.firstaidhero1.util.Lang;
import com.ahmed.firstaidhero1.util.TextNormalizer;

import java.util.ArrayList;
import java.util.List;

public class TreatedCaseRepository {

    private final AppDatabase helper;

    public TreatedCaseRepository(Context context) {
        this.helper = AppDatabase.getInstance(context);
    }

    // بترمي استثناء إذا فشل الإدخال، حتى يعرف المستدعي ويعرض رسالة بدل ما يظن إنه نجح
    // caseId لازم يكون من جدول emergencies (المفتاح الأجنبي بيرفض غير هيك)
    public long insert(long caseId, String description, String procedures, String dateIso,
                       @Nullable String photoPath) {
        ContentValues values = new ContentValues();
        values.put(AppDatabase.COL_CASE_ID, caseId);
        values.put(AppDatabase.COL_DESCRIPTION, description);
        values.put(AppDatabase.COL_PROCEDURES, procedures);
        values.put(AppDatabase.COL_TREATED_DATE, dateIso);
        values.put(AppDatabase.COL_PHOTO, photoPath); // null بتنخزّن NULL
        long id = helper.getWritableDatabase().insert(AppDatabase.TABLE_TREATED, null, values);
        if (id == -1) throw new SQLiteException("Insert into treated_cases failed");
        return id;
    }

    // الأحدث أولاً. كل كلمة لازم تظهر بالوصف أو الإجراءات أو التاريخ أو اسم الحالة
    // (AND بين الكلمات، OR بين الأعمدة). اسم الحالة وخطورتها بيجوا من JOIN مع emergencies
    public List<TreatedCase> search(@Nullable String rawQuery) {
        final String t = AppDatabase.TABLE_TREATED;
        final String e = AppDatabase.TABLE_EMERGENCIES;

        StringBuilder where = new StringBuilder();
        List<String> args = new ArrayList<>();
        String query = rawQuery == null ? "" : rawQuery.trim();
        for (String token : query.split("\\s+")) {
            if (token.isEmpty()) continue;
            String normalized = TextNormalizer.normalize(token);
            if (normalized.isEmpty()) normalized = token;

            where.append(where.length() == 0 ? " WHERE " : " AND ");
            where.append("(")
                    .append(t).append('.').append(AppDatabase.COL_DESCRIPTION).append(" LIKE ? ESCAPE '\\' OR ")
                    .append(t).append('.').append(AppDatabase.COL_PROCEDURES).append(" LIKE ? ESCAPE '\\' OR ")
                    .append(t).append('.').append(AppDatabase.COL_TREATED_DATE).append(" LIKE ? ESCAPE '\\' OR ")
                    .append(e).append('.').append(AppDatabase.COL_SEARCH).append(" LIKE ? ESCAPE '\\' OR ")
                    .append(e).append('.').append(AppDatabase.COL_SEARCH_EN).append(" LIKE ? ESCAPE '\\')");
            String raw = "%" + TextNormalizer.escapeLike(token) + "%";
            String norm = "%" + TextNormalizer.escapeLike(normalized) + "%";
            args.add(raw);
            args.add(raw);
            args.add(raw);
            args.add(norm);
            args.add(norm);
        }

        String sql = "SELECT "
                + t + "." + AppDatabase.COL_TREATED_ID + ", "
                + t + "." + AppDatabase.COL_CASE_ID + ", "
                + Lang.pick(e + "." + AppDatabase.COL_TITLE, e + "." + AppDatabase.COL_TITLE_EN) + " AS case_title, "
                + e + "." + AppDatabase.COL_DANGER + ", "
                + t + "." + AppDatabase.COL_DESCRIPTION + ", "
                + t + "." + AppDatabase.COL_PROCEDURES + ", "
                + t + "." + AppDatabase.COL_TREATED_DATE + ", "
                + t + "." + AppDatabase.COL_PHOTO
                + " FROM " + t
                + " JOIN " + e + " ON " + e + "." + AppDatabase.COL_CASE_ID + " = " + t + "." + AppDatabase.COL_CASE_ID
                + where
                + " ORDER BY " + t + "." + AppDatabase.COL_TREATED_DATE + " DESC, "
                + t + "." + AppDatabase.COL_TREATED_ID + " DESC";

        List<TreatedCase> results = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery(sql, args.isEmpty() ? null : args.toArray(new String[0]))) {
            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_TREATED_ID);
            int caseIdCol = c.getColumnIndexOrThrow(AppDatabase.COL_CASE_ID);
            int titleCol = c.getColumnIndexOrThrow("case_title");
            int dangerCol = c.getColumnIndexOrThrow(AppDatabase.COL_DANGER);
            int descCol = c.getColumnIndexOrThrow(AppDatabase.COL_DESCRIPTION);
            int procCol = c.getColumnIndexOrThrow(AppDatabase.COL_PROCEDURES);
            int dateCol = c.getColumnIndexOrThrow(AppDatabase.COL_TREATED_DATE);
            int photoCol = c.getColumnIndexOrThrow(AppDatabase.COL_PHOTO);

            while (c.moveToNext()) {
                results.add(new TreatedCase(
                        c.getLong(idCol),
                        c.getLong(caseIdCol),
                        c.getString(titleCol),
                        Severity.fromLevel(c.getInt(dangerCol)),
                        c.getString(descCol),
                        c.getString(procCol),
                        c.getString(dateCol),
                        c.getString(photoCol))); // null إذا NULL
            }
        }
        return results;
    }

    // بتحذف الصف وبترجّع مسار صورته (إن وُجد) حتى يحذف المستدعي الملف
    @Nullable
    public String delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] idArg = {String.valueOf(id)};
        String photo = null;
        try (Cursor c = db.query(AppDatabase.TABLE_TREATED,
                new String[]{AppDatabase.COL_PHOTO},
                AppDatabase.COL_TREATED_ID + " = ?", idArg, null, null, null)) {
            if (c.moveToFirst()) photo = c.getString(0);
        }
        db.delete(AppDatabase.TABLE_TREATED, AppDatabase.COL_TREATED_ID + " = ?", idArg);
        return photo;
    }
}
