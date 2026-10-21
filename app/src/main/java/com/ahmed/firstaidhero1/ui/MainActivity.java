package com.ahmed.firstaidhero1.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.adapter.CardAdapter;
import com.ahmed.firstaidhero1.adapter.HeaderAdapter;
import com.ahmed.firstaidhero1.model.CardItem;

import androidx.recyclerview.widget.ConcatAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, 0, systemBars.right,
                    Math.max(systemBars.bottom, ime.bottom));
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView logo = findViewById(R.id.logo);
        ViewGroup toolbarContent = findViewById(R.id.toolbarContent);
        SearchView searchView = findViewById(R.id.searchView);


        View plate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        plate.setBackgroundColor(Color.TRANSPARENT);


        OnBackPressedCallback backCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                searchView.clearFocus();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backCallback);


        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            TransitionManager.beginDelayedTransition(toolbarContent);
            logo.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
            backCallback.setEnabled(hasFocus);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });




        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        HeaderAdapter headerAdapter = new HeaderAdapter(v ->
                Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()); // حط هون منطق زرك القديم

        CardAdapter cardAdapter = new CardAdapter(createDummyItems(), item ->
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show());

        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, cardAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1; // الزر ياخد العمودين، والكروت عمود لكل وحدة
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(concatAdapter);
    }

    private List<CardItem> createDummyItems() {
        List<CardItem> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            list.add(new CardItem(
                    getString(R.string.card_title, i),
                    getString(R.string.card_subtitle, i),
                    R.mipmap.ic_launcher));
        }
        return list;
    }
}