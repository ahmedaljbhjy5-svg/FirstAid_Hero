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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ahmed.firstaidhero1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "home";
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_TREATED = "treated";
    private static final String TAG_STATS = "stats";
    private static final String[] TAGS = {TAG_HOME, TAG_CONTACTS, TAG_TREATED, TAG_STATS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            // الأعلى: مسؤولية AppBarLayout. الأسفل: الشريط السفلي بيتعامل مع شريط النظام لحاله،
            // فهون نحسب بس الكيبورد
            v.setPadding(systemBars.left, 0, systemBars.right, ime.bottom);
            // الكيبورد مفتوح = نخفي الشريط السفلي حتى ما يطلع فوق الكيبورد
            bottomNav.setVisibility(
                    insets.isVisible(WindowInsetsCompat.Type.ime()) ? View.GONE : View.VISIBLE);
            return insets;
        });

        // ---------- الشريط العلوي (نفس كودك السابق) ----------
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

        // ---------- التنقل بين التبويبات ----------
        setupFragments(savedInstanceState);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showTab(TAG_HOME);
            } else if (id == R.id.nav_contacts) {
                showTab(TAG_CONTACTS);
            } else if (id == R.id.nav_treated) {
                showTab(TAG_TREATED);
            } else if (id == R.id.nav_stats) {
                showTab(TAG_STATS);
            } else {
                return false;
            }
            return true;
        });
    }

    private void setupFragments(Bundle savedInstanceState) {
        // لو الشاشة انعادت (تدوير مثلاً)، الـ FragmentManager رجّع الـ Fragments لحاله
        // ولو أضفناهم مرة تانية بيصيروا نسختين فوق بعض
        if (savedInstanceState != null) return;

        Fragment contacts = new ContactsFragment();
        Fragment treated = new TreatedCasesFragment();
        Fragment stats = new StatisticsFragment();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainer, new HomeFragment(), TAG_HOME)
                .add(R.id.fragmentContainer, contacts, TAG_CONTACTS).hide(contacts)
                .add(R.id.fragmentContainer, treated, TAG_TREATED).hide(treated)
                .add(R.id.fragmentContainer, stats, TAG_STATS).hide(stats)
                .commit();
    }

    private void showTab(String selectedTag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().setReorderingAllowed(true);
        for (String tag : TAGS) {
            Fragment f = fm.findFragmentByTag(tag);
            if (f == null) continue;
            if (tag.equals(selectedTag)) ft.show(f);
            else ft.hide(f);
        }
        ft.commit();
    }
}