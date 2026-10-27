package com.ahmed.firstaidhero1.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.data.EmergencyRepository;
import com.ahmed.firstaidhero1.data.TreatedCaseRepository;
import com.ahmed.firstaidhero1.model.CaseOption;
import com.ahmed.firstaidhero1.util.PhotoStorage;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTreatedCaseActivity extends AppCompatActivity {

    private static final String STATE_CASE_ID = "state_case_id";
    private static final String STATE_DATE = "state_date";
    private static final String STATE_PHOTO = "state_photo";
    private static final String STATE_CAMERA_FILE = "state_camera_file";

    // خيط خلفي واحد: معالجة الصورة والكتابة بالقاعدة ما بتنفّذ على الـ Main Thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextInputLayout caseLayout;
    private MaterialAutoCompleteTextView caseInput;
    private View caseSeverityRow;
    private ImageView caseSeverityIcon;
    private TextView caseSeverityText;
    private TextInputLayout descriptionLayout;
    private TextInputLayout proceduresLayout;
    private TextInputLayout dateLayout;
    private TextInputEditText descriptionInput;
    private TextInputEditText proceduresInput;
    private TextInputEditText dateInput;
    private ShapeableImageView photoPreview;
    private MaterialButton addPhotoButton;
    private MaterialButton removePhotoButton;
    private MaterialButton saveButton;

    private List<CaseOption> caseOptions;
    private long selectedCaseId = -1L; // -1 = ما اختار حالة بعد

    private LocalDate selectedDate = LocalDate.now();
    @Nullable private Uri photoUri;
    @Nullable private File cameraFile;

    // لازم تتسجّل قبل أن تبدأ الـ Activity، لهيك هي حقول وليست داخل onCreate
    private final ActivityResultLauncher<PickVisualMediaRequest> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) setPhoto(uri);
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), taken -> {
                if (taken && cameraFile != null) {
                    setPhoto(uriFor(cameraFile));
                } else {
                    discardCameraFile();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_treated_case);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            // زر الحفظ بيرتفع فوق الكيبورد لما يفتح
            v.setPadding(systemBars.left, 0, systemBars.right,
                    Math.max(systemBars.bottom, ime.bottom));
            return insets;
        });

        ((Toolbar) findViewById(R.id.addToolbar)).setNavigationOnClickListener(v -> finish());

        caseLayout = findViewById(R.id.caseLayout);
        caseInput = findViewById(R.id.caseInput);
        caseSeverityRow = findViewById(R.id.caseSeverityRow);
        caseSeverityIcon = findViewById(R.id.caseSeverityIcon);
        caseSeverityText = findViewById(R.id.caseSeverityText);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        proceduresLayout = findViewById(R.id.proceduresLayout);
        dateLayout = findViewById(R.id.dateLayout);
        descriptionInput = findViewById(R.id.descriptionInput);
        proceduresInput = findViewById(R.id.proceduresInput);
        dateInput = findViewById(R.id.dateInput);
        photoPreview = findViewById(R.id.photoPreview);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        removePhotoButton = findViewById(R.id.removePhotoButton);
        saveButton = findViewById(R.id.saveButton);

        restoreState(savedInstanceState);
        setupCaseDropdown();
        updateDateField();
        updatePhotoUi();

        dateInput.setOnClickListener(v -> showDatePicker());
        dateLayout.setEndIconOnClickListener(v -> showDatePicker());
        addPhotoButton.setOnClickListener(v -> showPhotoSourceDialog());
        removePhotoButton.setOnClickListener(v -> clearPhoto());
        saveButton.setOnClickListener(v -> save());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_CASE_ID, selectedCaseId);
        outState.putString(STATE_DATE, selectedDate.toString());
        if (photoUri != null) outState.putString(STATE_PHOTO, photoUri.toString());
        if (cameraFile != null) outState.putString(STATE_CAMERA_FILE, cameraFile.getPath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // بيخلي المهمة الجارية تكمل ولا بيقبل جديدة
    }

    private void restoreState(@Nullable Bundle state) {
        if (state == null) return;
        selectedCaseId = state.getLong(STATE_CASE_ID, -1L);
        String date = state.getString(STATE_DATE);
        if (date != null) {
            try {
                selectedDate = LocalDate.parse(date);
            } catch (DateTimeParseException ignored) {
                // قيمة تالفة: نضل على تاريخ اليوم
            }
        }
        String photo = state.getString(STATE_PHOTO);
        if (photo != null) photoUri = Uri.parse(photo);
        String camera = state.getString(STATE_CAMERA_FILE);
        if (camera != null) cameraFile = new File(camera);
    }

    // ---------- اختيار الحالة (من قائمة الدليل فقط، بدون كتابة) ----------

    private void setupCaseDropdown() {
        caseOptions = new EmergencyRepository(this).getCaseOptions();
        caseInput.setAdapter(new CaseAdapter(this, caseOptions));
        caseInput.setOnItemClickListener((parent, view, position, rowId) -> {
            caseLayout.setError(null);
            showCase(caseOptions.get(position));
        });
        // بعد تدوير الشاشة أو تغيير اللغة: نرجّع الحالة المختارة (بالرقم، فاسمها بيتجدد بلغة الجهاز)
        for (CaseOption option : caseOptions) {
            if (option.getId() == selectedCaseId) {
                showCase(option);
                break;
            }
        }
    }

    private void showCase(CaseOption option) {
        selectedCaseId = option.getId();
        caseInput.setText(option.getTitle(), false); // false = بدون فلترة القائمة
        caseSeverityIcon.setImageResource(option.getSeverity().iconRes);
        caseSeverityText.setText(option.getSeverity().labelRes);
        caseSeverityRow.setVisibility(View.VISIBLE);
    }

    // القائمة لازم تعرض كل الحالات دائماً (الـ ArrayAdapter العادي بيفلتر حسب النص المختار)
    private static class CaseAdapter extends ArrayAdapter<CaseOption> {
        private final List<CaseOption> all;

        CaseAdapter(Context context, List<CaseOption> items) {
            super(context, android.R.layout.simple_dropdown_item_1line, items);
            this.all = items;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    results.values = all;
                    results.count = all.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }
    }

    // ---------- التاريخ ----------

    private void updateDateField() {
        dateInput.setText(selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
    }

    private void showDatePicker() {
        // MaterialDatePicker بيتعامل مع التواريخ كـ "منتصف الليل UTC"، فنحوّل بنفس المنطق
        long todayUtc = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long selectedUtc = selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.treated_date_title)
                .setSelection(selectedUtc)
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointBackward.before(todayUtc)) // لا مستقبل
                        .build())
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = Instant.ofEpochMilli(selection).atZone(ZoneOffset.UTC).toLocalDate();
            dateLayout.setError(null);
            updateDateField();
        });
        picker.show(getSupportFragmentManager(), "date_picker");
    }

    // ---------- الصورة ----------

    private void showPhotoSourceDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.treated_photo_source_title)
                .setItems(new CharSequence[]{
                        getString(R.string.photo_camera),
                        getString(R.string.photo_gallery)
                }, (dialog, which) -> {
                    if (which == 0) launchCamera();
                    else launchGallery();
                })
                .show();
    }

    private void launchGallery() {
        galleryLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void launchCamera() {
        try {
            File dir = new File(getCacheDir(), "camera");
            if (!dir.exists() && !dir.mkdirs()) throw new IOException("Cannot create " + dir);
            cameraFile = File.createTempFile("camera_", ".jpg", dir);
            cameraLauncher.launch(uriFor(cameraFile));
        } catch (IOException | ActivityNotFoundException e) {
            discardCameraFile();
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private Uri uriFor(File file) {
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
    }

    private void discardCameraFile() {
        if (cameraFile != null) {
            //noinspection ResultOfMethodCallIgnored
            cameraFile.delete();
            cameraFile = null;
        }
    }

    private void setPhoto(Uri uri) {
        photoUri = uri;
        updatePhotoUi();
    }

    private void clearPhoto() {
        photoUri = null;
        discardCameraFile();
        updatePhotoUi();
    }

    private void updatePhotoUi() {
        boolean hasPhoto = photoUri != null;
        photoPreview.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
        removePhotoButton.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
        addPhotoButton.setText(hasPhoto ? R.string.treated_change_photo : R.string.treated_add_photo);
        if (hasPhoto) {
            Glide.with(this).load(photoUri).centerCrop().into(photoPreview);
        }
    }

    // ---------- الحفظ ----------

    private void save() {
        String description = textOf(descriptionInput);
        String procedures = textOf(proceduresInput);

        caseLayout.setError(selectedCaseId < 0
                ? getString(R.string.treated_case_required) : null);
        descriptionLayout.setError(description.isEmpty()
                ? getString(R.string.treated_description_required) : null);
        proceduresLayout.setError(procedures.isEmpty()
                ? getString(R.string.treated_procedures_required) : null);
        dateLayout.setError(selectedDate.isAfter(LocalDate.now())
                ? getString(R.string.treated_date_future) : null);

        if (caseLayout.getError() != null
                || descriptionLayout.getError() != null
                || proceduresLayout.getError() != null
                || dateLayout.getError() != null) {
            return; // الشاشة تضل مفتوحة وتعرض الأخطاء
        }

        saveButton.setEnabled(false); // يمنع الضغط المزدوج
        final Uri photo = photoUri;
        final String dateIso = selectedDate.toString();
        final long caseId = selectedCaseId;

        executor.execute(() -> {
            String relativePath = null;
            try {
                if (photo != null) {
                    relativePath = PhotoStorage.saveScaledCopy(getApplicationContext(), photo);
                }
                new TreatedCaseRepository(getApplicationContext())
                        .insert(caseId, description, procedures, dateIso, relativePath);
            } catch (IOException | RuntimeException e) {
                // فشل أي جزء: نحذف الصورة اللي انحفظت (لتجنب ملف بدون صف) ونرجّع الزر
                PhotoStorage.delete(getApplicationContext(), relativePath);
                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    Toast.makeText(this, R.string.treated_save_failed, Toast.LENGTH_LONG).show();
                });
                return;
            }
            discardCameraFile();
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private static String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}

