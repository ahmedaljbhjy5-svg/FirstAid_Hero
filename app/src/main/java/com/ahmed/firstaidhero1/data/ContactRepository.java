package com.ahmed.firstaidhero1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private final Context context;
    private final AppDatabase helper;

    public ContactRepository(Context context) {
        this.context = context;
        this.helper = AppDatabase.getInstance(context);
    }

    // صف الإسعاف الثابت أولاً، وبعده أرقام المستخدم مرتبة بالاسم
    public List<Contact> getAll() {
        List<Contact> list = new ArrayList<>();
        list.add(new Contact(0,
                context.getString(R.string.ambulance_label),
                context.getString(R.string.emergency_number),
                true));

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
        ContentValues values = new ContentValues();
        values.put(AppDatabase.COL_NAME, name);
        values.put(AppDatabase.COL_PHONE, phone);
        return helper.getWritableDatabase().insert(AppDatabase.TABLE_CONTACTS, null, values);
    }

    public void delete(long id) {
        helper.getWritableDatabase().delete(
                AppDatabase.TABLE_CONTACTS,
                AppDatabase.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}