package com.ahmed.firstaidhero1.util;

import androidx.annotation.DrawableRes;

import com.ahmed.firstaidhero1.R;

public final class CaseImages {

    private CaseImages() {}

    // الرقم = case_id بجدول emergencies (نفس ترتيب ملف المراجع)
    @DrawableRes
    public static int forCase(long caseId) {
        switch ((int) caseId) {
            case 1:  return R.drawable.img_case_01_bleeding;
            case 2:  return R.drawable.img_case_02_choking;
            case 3:  return R.drawable.img_case_03_heart;
            case 4:  return R.drawable.img_case_04_snake;
            case 5:  return R.drawable.img_case_05_drowning;
            case 6:  return R.drawable.img_case_06_heat;
            case 7:  return R.drawable.img_case_07_poison;
            case 8:  return R.drawable.img_case_08_burn;
            case 9:  return R.drawable.img_case_09_fracture;
            case 10: return R.drawable.img_case_10_faint;
            case 11: return R.drawable.img_case_11_seizure;
            case 12: return R.drawable.img_case_12_cut;
            case 13: return R.drawable.img_case_13_nosebleed;
            case 14: return R.drawable.img_case_14_sprain;
            case 15: return R.drawable.img_case_15_insect;
            case 16: return R.drawable.img_case_16_stroke;
            case 17: return R.drawable.img_case_17_asthma;
            case 18: return R.drawable.img_case_18_allergy;
            case 19: return R.drawable.img_case_19_electric;
            case 20: return R.drawable.img_case_20_head;
            case 21: return R.drawable.img_case_21_eye;
            case 22: return R.drawable.img_case_22_animal;
            case 23: return R.drawable.img_case_23_sugar;
            case 24: return R.drawable.img_case_24_gas;
            default: return R.drawable.img_case_default; // حالة جديدة بدون رسمة
        }
    }
}