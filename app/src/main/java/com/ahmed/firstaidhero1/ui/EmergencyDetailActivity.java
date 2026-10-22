package com.ahmed.firstaidhero1.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.data.CaseRepository;
import com.ahmed.firstaidhero1.model.CaseDetail;
import com.ahmed.firstaidhero1.util.DialHelper;

public class EmergencyDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CASE_ID = "case_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency_detail);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1) نقرأ الـ id اللي أرسلته الرئيسية ونجيب الحالة
        int caseId = getIntent().getIntExtra(EXTRA_CASE_ID, -1);
        CaseDetail detail = new CaseRepository(this).getDetail(caseId);
        if (detail == null) {
            finish(); // id غير صالح: سكّر الشاشة بدل ما تكرش
            return;
        }

        // 2) الشريط العلوي
        Toolbar toolbar = findViewById(R.id.detailToolbar);
        toolbar.setTitle(detail.getTitle());
        toolbar.setNavigationOnClickListener(v -> finish());

        // 3) الأجزاء الثابتة
        ImageView severityIcon = findViewById(R.id.detailSeverity);
        severityIcon.setImageResource(detail.getSeverity().iconRes);
        severityIcon.setContentDescription(getString(detail.getSeverity().labelRes));

        ((TextView) findViewById(R.id.detailDescription)).setText(detail.getDescription());
        ((TextView) findViewById(R.id.detailHelpText)).setText(detail.getHelpWhen());

        // 4) القوائم: صف لكل خطوة وصف لكل تحذير
        LayoutInflater inflater = LayoutInflater.from(this);

        LinearLayout stepsContainer = findViewById(R.id.stepsContainer);
        for (int i = 0; i < detail.getSteps().size(); i++) {
            View row = inflater.inflate(R.layout.item_step, stepsContainer, false);
            ((TextView) row.findViewById(R.id.stepNumber)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.stepText)).setText(detail.getSteps().get(i));
            stepsContainer.addView(row);
        }

        LinearLayout dontsContainer = findViewById(R.id.dontsContainer);
        for (String text : detail.getDonts()) {
            View row = inflater.inflate(R.layout.item_dont, dontsContainer, false);
            ((TextView) row.findViewById(R.id.dontText)).setText(text);
            dontsContainer.addView(row);
        }

        // 5) زر الاتصال السفلي
        findViewById(R.id.headerButton).setOnClickListener(v -> DialHelper.dialEmergency(this));
    }
}