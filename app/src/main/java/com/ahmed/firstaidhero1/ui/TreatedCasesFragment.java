package com.ahmed.firstaidhero1.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.adapter.TreatedCaseAdapter;
import com.ahmed.firstaidhero1.data.TreatedCaseRepository;
import com.ahmed.firstaidhero1.model.TreatedCase;
import com.ahmed.firstaidhero1.util.PhotoStorage;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class TreatedCasesFragment extends Fragment {

    private TreatedCaseRepository repository;
    private TreatedCaseAdapter adapter;
    private TextView emptyView;
    private String currentQuery = "";

    // نتيجة شاشة الإضافة: لو انحفظت حالة، نعيد تحميل القائمة
    private final ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) reload();
            });

    public TreatedCasesFragment() {
        super(R.layout.fragment_treated_cases);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new TreatedCaseRepository(requireContext());
        emptyView = view.findViewById(R.id.treatedEmpty);

        adapter = new TreatedCaseAdapter(this::confirmDelete);
        RecyclerView recyclerView = view.findViewById(R.id.treatedRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        TextInputEditText searchInput = view.findViewById(R.id.treatedSearchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString();
                reload();
            }
        });

        view.findViewById(R.id.addTreatedFab).setOnClickListener(v ->
                addLauncher.launch(new Intent(requireContext(), AddTreatedCaseActivity.class)));

        reload();
    }

    private void reload() {
        List<TreatedCase> cases = repository.search(currentQuery);
        adapter.submitList(cases);
        emptyView.setText(currentQuery.trim().isEmpty()
                ? R.string.treated_empty : R.string.search_no_results);
        emptyView.setVisibility(cases.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmDelete(TreatedCase treatedCase) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.treated_delete_title)
                .setMessage(R.string.treated_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    String photoPath = repository.delete(treatedCase.getId());
                    PhotoStorage.delete(requireContext(), photoPath);
                    reload();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}