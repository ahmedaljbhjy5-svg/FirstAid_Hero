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
import com.ahmed.firstaidhero1.data.EmergencyRepository;
import com.ahmed.firstaidhero1.model.CaseDetail;
import com.ahmed.firstaidhero1.util.CaseImages;
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

        long caseId = getIntent().getLongExtra(EXTRA_CASE_ID, -1L);
        ((ImageView) findViewById(R.id.detailImage)).setImageResource(CaseImages.forCase(caseId));
        CaseDetail detail = new EmergencyRepository(this).getDetail(caseId);
        if (detail == null) {
            finish(); // رقم غير صالح: نسكّر الشاشة بدل ما ننهار
            return;
        }

        Toolbar toolbar = findViewById(R.id.detailToolbar);
        toolbar.setTitle(detail.getTitle());
        toolbar.setNavigationOnClickListener(v -> finish());

        ImageView severityIcon = findViewById(R.id.detailSeverity);
        severityIcon.setImageResource(detail.getSeverity().iconRes);
        ((TextView) findViewById(R.id.detailSeverityLabel)).setText(detail.getSeverity().labelRes);
        ((TextView) findViewById(R.id.detailSymptoms)).setText(detail.getSymptoms());
        ((TextView) findViewById(R.id.detailSource))
                .setText(getString(R.string.detail_source, detail.getSource()));

        LinearLayout stepsContainer = findViewById(R.id.stepsContainer);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < detail.getSteps().size(); i++) {
            View row = inflater.inflate(R.layout.item_step, stepsContainer, false);
            ((TextView) row.findViewById(R.id.stepNumber)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.stepText)).setText(detail.getSteps().get(i));
            stepsContainer.addView(row);
        }

        findViewById(R.id.headerButton).setOnClickListener(v -> DialHelper.dialEmergency(this));
    }
}

