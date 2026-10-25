package com.ahmed.firstaidhero1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.annotation.Nullable;

import com.ahmed.firstaidhero1.model.TreatedCase;
import com.ahmed.firstaidhero1.util.TextNormalizer;

import java.util.ArrayList;
import java.util.List;

public class TreatedCaseRepository {

    private final AppDatabase helper;

    public TreatedCaseRepository(Context context) {
        this.helper = AppDatabase.getInstance(context);
    }

    // بترمي استثناء إذا فشل الإدخال، حتى يعرف المستدعي ويعرض رسالة بدل ما يظن إنه نجح
    public long insert(String description, String procedures, String dateIso,
                       @Nullable String photoPath) {
        ContentValues values = new ContentValues();
        values.put(AppDatabase.COL_DESCRIPTION, description);
        values.put(AppDatabase.COL_PROCEDURES, procedures);
        values.put(AppDatabase.COL_TREATED_DATE, dateIso);
        values.put(AppDatabase.COL_PHOTO, photoPath); // null بتنخزّن NULL
        long id = helper.getWritableDatabase().insert(AppDatabase.TABLE_TREATED, null, values);
        if (id == -1) throw new SQLiteException("Insert into treated_cases failed");
        return id;
    }

    // الأحدث أولاً. كل كلمة لازم تظهر بالوصف أو الإجراءات أو التاريخ (AND بين الكلمات، OR بين الأعمدة)
    public List<TreatedCase> search(@Nullable String rawQuery) {
        StringBuilder where = new StringBuilder();
        List<String> args = new ArrayList<>();
        String query = rawQuery == null ? "" : rawQuery.trim();
        for (String token : query.split("\\s+")) {
            if (token.isEmpty()) continue;
            if (where.length() > 0) where.append(" AND ");
            where.append("(")
                    .append(AppDatabase.COL_DESCRIPTION).append(" LIKE ? ESCAPE '\\' OR ")
                    .append(AppDatabase.COL_PROCEDURES).append(" LIKE ? ESCAPE '\\' OR ")
                    .append(AppDatabase.COL_TREATED_DATE).append(" LIKE ? ESCAPE '\\')");
            String like = "%" + TextNormalizer.escapeLike(token) + "%";
            args.add(like);
            args.add(like);
            args.add(like);
        }

        List<TreatedCase> results = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(
                AppDatabase.TABLE_TREATED,
                null,
                where.length() == 0 ? null : where.toString(),
                args.isEmpty() ? null : args.toArray(new String[0]),
                null, null,
                AppDatabase.COL_TREATED_DATE + " DESC, " + AppDatabase.COL_TREATED_ID + " DESC")) {

            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_TREATED_ID);
            int descCol = c.getColumnIndexOrThrow(AppDatabase.COL_DESCRIPTION);
            int procCol = c.getColumnIndexOrThrow(AppDatabase.COL_PROCEDURES);
            int dateCol = c.getColumnIndexOrThrow(AppDatabase.COL_TREATED_DATE);
            int photoCol = c.getColumnIndexOrThrow(AppDatabase.COL_PHOTO);

            while (c.moveToNext()) {
                results.add(new TreatedCase(
                        c.getLong(idCol),
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