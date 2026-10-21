package com.ahmed.firstaidhero1.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;   // تغيّر
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.adapter.CardAdapter;
import com.ahmed.firstaidhero1.adapter.HeaderAdapter;
import com.ahmed.firstaidhero1.model.CardItem;
import com.ahmed.firstaidhero1.model.Severity;               // تغيّر

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        HeaderAdapter headerAdapter = new HeaderAdapter(v ->
                Toast.makeText(requireContext(), "Button clicked", Toast.LENGTH_SHORT).show());

        CardAdapter cardAdapter = new CardAdapter(createDummyItems(), item ->
                Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show());

        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, cardAdapter);

        // تغيّر: قائمة بعمود واحد، فما عدنا بحاجة GridLayoutManager ولا SpanSizeLookup
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(concatAdapter);
    }

    private List<CardItem> createDummyItems() {
        List<CardItem> list = new ArrayList<>();
        Severity[] levels = Severity.values();                 // تغيّر
        for (int i = 1; i <= 10; i++) {
            list.add(new CardItem(
                    getString(R.string.card_title, i),
                    getString(R.string.card_subtitle, i),
                    levels[i % levels.length],                 // تغيّر: يدوّر على المستويات التلاتة
                    R.mipmap.ic_launcher));
        }
        return list;
    }
}