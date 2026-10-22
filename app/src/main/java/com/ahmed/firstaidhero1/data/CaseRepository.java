package com.ahmed.firstaidhero1.data;

import android.content.Context;
import android.content.res.Resources;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.CardItem;
import com.ahmed.firstaidhero1.model.CaseDetail;
import com.ahmed.firstaidhero1.model.Severity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseRepository {

    // تعريف حالة واحدة: أرقام الموارد فقط، النصوص الفعلية بتتجاب حسب لغة الجهاز
    private static final class CaseDef {
        final int id;
        final int titleRes;
        final int descRes;
        final Severity severity;
        final int helpRes;
        final int stepsRes;
        final int dontsRes;

        CaseDef(int id, int titleRes, int descRes, Severity severity,
                int helpRes, int stepsRes, int dontsRes) {
            this.id = id;
            this.titleRes = titleRes;
            this.descRes = descRes;
            this.severity = severity;
            this.helpRes = helpRes;
            this.stepsRes = stepsRes;
            this.dontsRes = dontsRes;
        }
    }

    private static final CaseDef[] CASES = {
            new CaseDef(1, R.string.case_bleeding_title, R.string.case_bleeding_desc, Severity.HIGH,
                    R.string.case_bleeding_help, R.array.case_bleeding_steps, R.array.case_bleeding_donts),
            new CaseDef(2, R.string.case_burns_title, R.string.case_burns_desc, Severity.MEDIUM,
                    R.string.case_burns_help, R.array.case_burns_steps, R.array.case_burns_donts),
            new CaseDef(3, R.string.case_cut_title, R.string.case_cut_desc, Severity.LOW,
                    R.string.case_cut_help, R.array.case_cut_steps, R.array.case_cut_donts)
    };

    private final Context context;

    public CaseRepository(Context context) {
        this.context = context;
    }

    // للقائمة: ملخصات فقط
    public List<CardItem> getCardItems() {
        List<CardItem> list = new ArrayList<>();
        for (CaseDef def : CASES) {
            list.add(new CardItem(
                    def.id,
                    context.getString(def.titleRes),
                    context.getString(def.descRes),
                    def.severity,
                    R.mipmap.ic_launcher));
        }
        return list;
    }

    // للتفاصيل: حالة كاملة، أو null إذا الرقم غير موجود
    public CaseDetail getDetail(int id) {
        Resources res = context.getResources();
        for (CaseDef def : CASES) {
            if (def.id == id) {
                return new CaseDetail(
                        def.id,
                        context.getString(def.titleRes),
                        context.getString(def.descRes),
                        def.severity,
                        context.getString(def.helpRes),
                        Arrays.asList(res.getStringArray(def.stepsRes)),
                        Arrays.asList(res.getStringArray(def.dontsRes)));
            }
        }
        return null;
    }
}