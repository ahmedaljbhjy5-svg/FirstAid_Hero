package com.ahmed.firstaidhero1.util;

import java.util.Locale;


public final class Lang {

    private Lang() {}

    public static boolean isArabic() {
        return "ar".equals(Locale.getDefault().getLanguage());
    }


    public static String pick(String arabicColumn, String englishColumn) {
        if (isArabic()) return arabicColumn;
        return "COALESCE(NULLIF(" + englishColumn + ", ''), " + arabicColumn + ")";
    }
}
