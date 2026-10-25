package com.ahmed.firstaidhero1.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public final class PhotoStorage {

    private static final String DIR = "photos";
    private static final int MAX_SIDE_PX = 1280;
    private static final int JPEG_QUALITY = 85;

    private PhotoStorage() {}

    public static File resolve(Context context, String relativePath) {
        return new File(context.getFilesDir(), relativePath);
    }

    public static void delete(Context context, @Nullable String relativePath) {
        if (relativePath == null) return;
        File file = resolve(context, relativePath);
        if (file.exists() && !file.delete()) file.deleteOnExit();
    }

    // بتقرأ الصورة من أي Uri، بتصغّرها وبتصحح اتجاهها، وبتحفظها JPEG. بترجّع المسار النسبي
    public static String saveScaledCopy(Context context, Uri source) throws IOException {
        ContentResolver resolver = context.getContentResolver();

        // 1) قياس الأبعاد فقط، بدون تحميل الصورة بالذاكرة
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = open(resolver, source)) {
            BitmapFactory.decodeStream(in, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw new IOException("Not a valid image: " + source);
        }

        // 2) تحميل نسخة مصغّرة بنسبة قوة 2 (أسرع وأوفر بالذاكرة)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight);
        Bitmap decoded;
        try (InputStream in = open(resolver, source)) {
            decoded = BitmapFactory.decodeStream(in, null, options);
        }
        if (decoded == null) throw new IOException("Could not decode image");

        // 3) تدوير حسب EXIF + تصغير للحجم النهائي بخطوة وحدة
        int rotation = readRotation(resolver, source);
        float scale = Math.min(1f, (float) MAX_SIDE_PX / Math.max(decoded.getWidth(), decoded.getHeight()));
        Bitmap result = decoded;
        if (rotation != 0 || scale < 1f) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            matrix.postScale(scale, scale);
            result = Bitmap.createBitmap(decoded, 0, 0, decoded.getWidth(), decoded.getHeight(), matrix, true);
            if (result != decoded) decoded.recycle();
        }

        // 4) حفظ JPEG بمجلد التطبيق الخاص
        File dir = new File(context.getFilesDir(), DIR);
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Cannot create " + dir);
        String relative = DIR + "/" + UUID.randomUUID() + ".jpg";
        File dest = resolve(context, relative);
        try (OutputStream out = new FileOutputStream(dest)) {
            if (!result.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)) {
                throw new IOException("Compress failed");
            }
        } catch (IOException e) {
            dest.delete();
            throw e;
        } finally {
            result.recycle();
        }
        return relative;
    }

    private static InputStream open(ContentResolver resolver, Uri uri) throws IOException {
        InputStream in = resolver.openInputStream(uri);
        if (in == null) throw new IOException("Cannot open " + uri);
        return in;
    }

    // أكبر قوة 2 بتخلي الأبعاد لسا >= الحجم المطلوب
    private static int sampleSize(int width, int height) {
        int sample = 1;
        while (Math.max(width, height) / (sample * 2) >= MAX_SIDE_PX) {
            sample *= 2;
        }
        return sample;
    }

    private static int readRotation(ContentResolver resolver, Uri uri) {
        try (InputStream in = resolver.openInputStream(uri)) {
            if (in == null) return 0;
            ExifInterface exif = new ExifInterface(in);
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            return 0; // ما قدرنا نقرأ EXIF: نعتبر الصورة معتدلة
        }
    }
}