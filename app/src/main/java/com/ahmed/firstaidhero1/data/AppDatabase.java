package com.ahmed.firstaidhero1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "firstaid.db";
    private static final int DB_VERSION = 1;

    // جدول الحالات
    public static final String TABLE_EMERGENCIES = "emergencies";
    public static final String COL_CASE_ID = "case_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DANGER = "danger_level";
    public static final String COL_SYMPTOMS = "symptoms";
    public static final String COL_SEARCH = "search_text";

    // جدول الخطوات
    public static final String TABLE_STEPS = "steps";
    public static final String COL_STEP_NUMBER = "step_number";
    public static final String COL_INSTRUCTION = "instruction_text";

    // جدول الأرقام المخصصة
    public static final String TABLE_CONTACTS = "custom_contacts";
    public static final String COL_ID = "contact_id";
    public static final String COL_NAME = "contact_name";
    public static final String COL_PHONE = "phone_number";

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

    // بتنسخ القاعدة الجاهزة من assets مرة وحدة: أول تشغيل فقط
    private static void copyFromAssetsIfNeeded(Context context) {
        File dbFile = context.getDatabasePath(DB_NAME);
        if (dbFile.exists()) return;

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