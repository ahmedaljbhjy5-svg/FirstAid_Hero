package com.ahmed.firstaidhero1.util;

import java.util.Locale;

public final class TextNormalizer {

    private TextNormalizer() {}

    // لازم تطابق دالة normalize بملف build_db.py بالضبط، وإلا ما بيتطابق البحث
    public static String normalize(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toLowerCase(Locale.ROOT).toCharArray()) {
            if ((c >= '\u064B' && c <= '\u0652') || c == '\u0640') continue; // تشكيل وتطويل
            switch (c) {
                case '\u0623': // أ
                case '\u0625': // إ
                case '\u0622': // آ
                    sb.append('\u0627'); // ا
                    break;
                case '\u0649': // ى
                    sb.append('\u064A'); // ي
                    break;
                case '\u0629': // ة
                    sb.append('\u0647'); // ه
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString().trim().replaceAll("\\s+", " ");
    }

    // بيعطّل الرموز الخاصة بـ LIKE (% و _) حتى تُعامل كحروف عادية
    public static String escapeLike(String token) {
        return token.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}