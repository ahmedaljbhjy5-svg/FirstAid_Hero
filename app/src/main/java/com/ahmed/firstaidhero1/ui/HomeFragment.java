package com.ahmed.firstaidhero1.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.ahmed.firstaidhero1.data.CaseRepository;
import com.ahmed.firstaidhero1.util.DialHelper;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        HeaderAdapter headerAdapter = new HeaderAdapter(v ->
                DialHelper.dialEmergency(requireContext()));

        CaseRepository repository = new CaseRepository(requireContext());
        CardAdapter cardAdapter = new CardAdapter(repository.getCardItems(), item -> {
            Intent intent = new Intent(requireContext(), EmergencyDetailActivity.class);
            intent.putExtra(EmergencyDetailActivity.EXTRA_CASE_ID, item.getId());
            startActivity(intent);
        });

        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, cardAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(concatAdapter);
    }
}