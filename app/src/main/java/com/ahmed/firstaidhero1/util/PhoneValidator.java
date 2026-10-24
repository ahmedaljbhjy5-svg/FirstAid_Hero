package com.ahmed.firstaidhero1.util;

import java.util.regex.Pattern;

public final class PhoneValidator {

    // + اختيارية وبالبداية فقط، بعدها أرقام ومسافات وشرطات وأقواس
    private static final Pattern ALLOWED = Pattern.compile("^\\+?[0-9\\s\\-()]{3,20}$");

    private PhoneValidator() {}

    public static boolean isValid(String phone) {
        if (phone == null || !ALLOWED.matcher(phone).matches()) return false;
        int digits = canonical(phone).replace("+", "").length();
        return digits >= 3 && digits <= 15; // 15 = أقصى طول لرقم دولي (معيار E.164)
    }

    // الرقم بدون مسافات وشرطات وأقواس: للمقارنة واكتشاف التكرار
    public static String canonical(String phone) {
        return phone.replaceAll("[\\s\\-()]", "");
    }
}