package com.ahmed.firstaidhero1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.Contact;
import com.ahmed.firstaidhero1.util.PhoneValidator;

import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private final Context context;
    private final AppDatabase helper;

    public ContactRepository(Context context) {
        this.context = context;
        this.helper = AppDatabase.getInstance(context);
    }

    // الأرقام العامة الثابتة أولاً (id سالب = غير مخزّنة بالقاعدة)، وبعدها أرقام المستخدم بالاسم
    public List<Contact> getAll() {
        List<Contact> list = new ArrayList<>();
        list.add(new Contact(-1, context.getString(R.string.ambulance_label),
                context.getString(R.string.number_ambulance), true));
        list.add(new Contact(-2, context.getString(R.string.fire_label),
                context.getString(R.string.number_fire), true));
        list.add(new Contact(-3, context.getString(R.string.police_label),
                context.getString(R.string.number_police), true));

        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(
                AppDatabase.TABLE_CONTACTS,
                null, null, null, null, null,
                AppDatabase.COL_NAME + " COLLATE NOCASE ASC")) {

            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_ID);
            int nameCol = c.getColumnIndexOrThrow(AppDatabase.COL_NAME);
            int phoneCol = c.getColumnIndexOrThrow(AppDatabase.COL_PHONE);

            while (c.moveToNext()) {
                list.add(new Contact(
                        c.getLong(idCol),
                        c.getString(nameCol),
                        c.getString(phoneCol),
                        false));
            }
        }
        return list;
    }

    public long insert(String name, String phone) {
        return helper.getWritableDatabase()
                .insert(AppDatabase.TABLE_CONTACTS, null, values(name, phone));
    }

    // بترجع عدد الصفوف المعدّلة (0 = الصف انحذف قبل التعديل)
    public int update(long id, String name, String phone) {
        return helper.getWritableDatabase().update(
                AppDatabase.TABLE_CONTACTS,
                values(name, phone),
                AppDatabase.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void delete(long id) {
        helper.getWritableDatabase().delete(
                AppDatabase.TABLE_CONTACTS,
                AppDatabase.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // هل الرقم محفوظ مسبقاً؟ excludeId = الصف اللي عم نعدّله (حتى ما يُعتبر مكرراً مع نفسه)
    public boolean phoneExists(String phone, long excludeId) {
        String target = PhoneValidator.canonical(phone);
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query(
                AppDatabase.TABLE_CONTACTS,
                new String[]{AppDatabase.COL_ID, AppDatabase.COL_PHONE},
                null, null, null, null, null)) {

            int idCol = c.getColumnIndexOrThrow(AppDatabase.COL_ID);
            int phoneCol = c.getColumnIndexOrThrow(AppDatabase.COL_PHONE);
            while (c.moveToNext()) {
                if (c.getLong(idCol) != excludeId
                        && PhoneValidator.canonical(c.getString(phoneCol)).equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ContentValues values(String name, String phone) {
        ContentValues values = new ContentValues();
        values.put(AppDatabase.COL_NAME, name);
        values.put(AppDatabase.COL_PHONE, phone);
        return values;
    }
}