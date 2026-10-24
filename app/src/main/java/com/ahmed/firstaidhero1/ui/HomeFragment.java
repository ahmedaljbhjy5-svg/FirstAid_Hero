package com.ahmed.firstaidhero1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.adapter.CardAdapter;
import com.ahmed.firstaidhero1.adapter.HeaderAdapter;
import com.ahmed.firstaidhero1.data.EmergencyRepository;
import com.ahmed.firstaidhero1.model.CardItem;
import com.ahmed.firstaidhero1.util.DialHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class HomeFragment extends Fragment {

    private EmergencyRepository repository;
    private CardAdapter cardAdapter;
    private View emptyView;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new EmergencyRepository(requireContext());
        emptyView = view.findViewById(R.id.homeEmpty);

        HeaderAdapter headerAdapter = new HeaderAdapter(v ->
                DialHelper.dialEmergency(requireContext()));
        cardAdapter = new CardAdapter(this::openDetail);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ConcatAdapter(headerAdapter, cardAdapter));

        TextInputEditText searchInput = view.findViewById(R.id.homeSearchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                runSearch(s.toString()); // مع كل حرف
            }
        });

        runSearch(searchInput.getText() == null ? "" : searchInput.getText().toString());
    }

    private void runSearch(String query) {
        List<CardItem> results = repository.search(query);
        cardAdapter.submitList(results);
        emptyView.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openDetail(CardItem item) {
        Intent intent = new Intent(requireContext(), EmergencyDetailActivity.class);
        intent.putExtra(EmergencyDetailActivity.EXTRA_CASE_ID, item.getId());
        startActivity(intent);
    }
}