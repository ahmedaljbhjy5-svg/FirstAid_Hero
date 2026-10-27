package com.ahmed.firstaidhero1.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "firstaid.db";
    // لازم تطابق PRAGMA user_version داخل firstaid.db (ينتجها build_db.py)
    private static final int DB_VERSION = 2;

    // جدول الحالات
    public static final String TABLE_EMERGENCIES = "emergencies";
    public static final String COL_CASE_ID = "case_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DANGER = "danger_level";
    public static final String COL_SYMPTOMS = "symptoms";
    public static final String COL_SEARCH = "search_text";
    public static final String COL_TITLE_EN = "title_en";
    public static final String COL_SYMPTOMS_EN = "symptoms_en";
    public static final String COL_SEARCH_EN = "search_text_en";
    public static final String COL_SOURCE = "source";
    public static final String COL_SOURCE_EN = "source_en";

    // جدول الخطوات
    public static final String TABLE_STEPS = "steps";
    public static final String COL_STEP_NUMBER = "step_number";
    public static final String COL_INSTRUCTION = "instruction_text";
    public static final String COL_INSTRUCTION_EN = "instruction_text_en";

    // جدول الأرقام المخصصة
    public static final String TABLE_CONTACTS = "custom_contacts";
    public static final String COL_ID = "contact_id";
    public static final String COL_NAME = "contact_name";
    public static final String COL_PHONE = "phone_number";

    // جدول الحالات المعالجة
    public static final String TABLE_TREATED = "treated_cases";
    public static final String COL_TREATED_ID = "treated_id";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_TREATED_DATE = "treated_date";
    public static final String COL_PHOTO = "photo_path";
    public static final String COL_PROCEDURES = "procedures";

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            Context app = context.getApplicationContext();
            copyFromAssetsIfNeeded(app);
            instance = new AppDatabase(app);
        }
        return instance;
    }

    private AppDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // بتنسخ القاعدة الجاهزة من assets: أول تشغيل، أو إذا الملف الموجود قديم/تالف.
    // تنبيه: استبدال ملف قديم بيحذف الحالات المعالجة المخزّنة فيه (مقبول أثناء التطوير،
    // وللتسليم النهائي بنثبّت DB_VERSION ولا نغيّره)
    private static void copyFromAssetsIfNeeded(Context context) {
        File dbFile = context.getDatabasePath(DB_NAME);
        if (dbFile.exists()) {
            if (isCurrent(dbFile)) return;
            context.deleteDatabase(DB_NAME);
        }

        File dir = dbFile.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Cannot create database directory: " + dir);
        }

        // ننسخ لملف مؤقت أولاً، وبعدها نعيد تسميته. لو انقطع النسخ بالنص ما بيضل ملف تالف
        File tmp = new File(dir, DB_NAME + ".tmp");
        try (InputStream in = context.getAssets().open(DB_NAME);
             OutputStream out = new FileOutputStream(tmp)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            tmp.delete();
            throw new IllegalStateException("Failed to copy " + DB_NAME + " from assets", e);
        }

        if (!tmp.renameTo(dbFile)) {
            tmp.delete();
            throw new IllegalStateException("Failed to install " + DB_NAME);
        }
    }

    // الملف الموجود صالح إذا إصداره مطابق وفيه الأعمدة الأساسية (بدونها ما بتشتغل الاستعلامات)
    private static boolean isCurrent(File dbFile) {
        try (SQLiteDatabase db = SQLiteDatabase.openDatabase(
                dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY)) {
            return db.getVersion() == DB_VERSION
                    && hasColumn(db, TABLE_EMERGENCIES, COL_TITLE_EN)
                    && hasColumn(db, TABLE_STEPS, COL_INSTRUCTION_EN)
                    && hasColumn(db, TABLE_TREATED, COL_CASE_ID);
        } catch (SQLiteException e) {
            return false; // ملف تالف
        }
    }

    private static boolean hasColumn(SQLiteDatabase db, String table, String column) {
        try (Cursor c = db.rawQuery("PRAGMA table_info(" + table + ")", null)) {
            int nameCol = c.getColumnIndexOrThrow("name");
            while (c.moveToNext()) {
                if (column.equals(c.getString(nameCol))) return true;
            }
        }
        return false;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // SQLite بيتجاهل المفاتيح الأجنبية ما لم نفعّلها
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // فاضية عن قصد: الجداول جاهزة داخل الملف المنسوخ (user_version = 1)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // لاحقاً: ترفع DB_VERSION وتحط هون ALTER TABLE أو CREATE TABLE للتغييرات
    }
}

