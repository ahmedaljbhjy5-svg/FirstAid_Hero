package com.ahmed.firstaidhero1.util;

import java.util.Locale;

// لغة المحتوى المخزّن بالقاعدة: عربي إذا لغة الجهاز عربية، وإلا إنجليزي
public final class Lang {

    private Lang() {}

    public static boolean isArabic() {
        return "ar".equals(Locale.getDefault().getLanguage());
    }

    // بترجّع تعبير SQL بيختار عمود اللغة الحالية. للإنجليزي: إذا العمود فاضي نرجع للعربي
    // مثال: Lang.pick("title", "title_en") أو Lang.pick("e.title", "e.title_en")
    public static String pick(String arabicColumn, String englishColumn) {
        if (isArabic()) return arabicColumn;
        return "COALESCE(NULLIF(" + englishColumn + ", ''), " + arabicColumn + ")";
    }
}
