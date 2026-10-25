package com.ahmed.firstaidhero1.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.data.StatsRepository;
import com.ahmed.firstaidhero1.model.MonthStat;
import com.ahmed.firstaidhero1.model.SeverityStat;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment {

    private StatsRepository repository;
    private LinearLayout severityContainer;
    private LinearLayout monthContainer;
    private TextView emergenciesTotal;
    private TextView treatedTotal;
    private TextView monthEmpty;

    public StatisticsFragment() {
        super(R.layout.fragment_statistics);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new StatsRepository(requireContext());
        severityContainer = view.findViewById(R.id.statsSeverityContainer);
        monthContainer = view.findViewById(R.id.statsMonthContainer);
        emergenciesTotal = view.findViewById(R.id.statsEmergenciesTotal);
        treatedTotal = view.findViewById(R.id.statsTreatedTotal);
        monthEmpty = view.findViewById(R.id.statsMonthEmpty);

        loadStats();
    }

    // التبويبات عندنا بتُخفى وتُظهر (hide/show) وما بتنهدم، فما بيتنفذ onViewCreated مرة تانية.
    // هالدالة بتنادي كل ما التبويب يظهر، حتى نشوف الحالات اللي انضافت من تبويب تاني
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) loadStats();
    }

    private void loadStats() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        // ---- الحالات حسب الخطورة ----
        List<SeverityStat> severityStats = repository.getSeverityStats();
        int total = 0;
        for (SeverityStat stat : severityStats) total += stat.getCount();
        emergenciesTotal.setText(getString(R.string.stats_total_cases, total));

        severityContainer.removeAllViews();
        for (SeverityStat stat : severityStats) {
            addRow(severityContainer, inflater,
                    stat.getSeverity().iconRes,
                    getString(stat.getSeverity().nameRes),
                    stat.getCount(), total,
                    ContextCompat.getColor(requireContext(), stat.getSeverity().colorRes));
        }

        // ---- الحالات المعالجة ----
        treatedTotal.setText(String.valueOf(repository.getTreatedTotal()));

        List<MonthStat> months = repository.getTreatedPerMonth();
        monthContainer.removeAllViews();
        monthEmpty.setVisibility(months.isEmpty() ? View.VISIBLE : View.GONE);

        int maxMonth = 0;
        for (MonthStat stat : months) maxMonth = Math.max(maxMonth, stat.getCount());

        int primary = MaterialColors.getColor(monthContainer, androidx.appcompat.R.attr.colorPrimary);
        for (MonthStat stat : months) {
            addRow(monthContainer, inflater, 0,
                    formatMonth(stat.getMonth()), stat.getCount(), maxMonth, primary);
        }
    }

    // iconRes = 0 يعني بدون أيقونة
    private void addRow(LinearLayout container, LayoutInflater inflater, int iconRes,
                        String label, int count, int max, int color) {
        View row = inflater.inflate(R.layout.item_stat_row, container, false);

        ImageView icon = row.findViewById(R.id.statIcon);
        if (iconRes == 0) icon.setVisibility(View.GONE);
        else icon.setImageResource(iconRes);

        ((TextView) row.findViewById(R.id.statLabel)).setText(label);
        ((TextView) row.findViewById(R.id.statCount)).setText(String.valueOf(count));

        LinearProgressIndicator bar = row.findViewById(R.id.statBar);
        bar.setMax(Math.max(max, 1)); // max = 0 غير مسموح
        bar.setIndicatorColor(color);
        bar.setProgressCompat(count, false);

        container.addView(row);
    }

    private static String formatMonth(String yearMonth) {
        try {
            return YearMonth.parse(yearMonth)
                    .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()));
        } catch (DateTimeParseException e) {
            return yearMonth; // صيغة غير متوقعة: نعرضها كما هي بدل ما ننهار
        }
    }
}