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
import com.ahmed.firstaidhero1.model.CaseStat;
import com.ahmed.firstaidhero1.model.DayStat;
import com.ahmed.firstaidhero1.model.MonthStat;
import com.ahmed.firstaidhero1.model.SeverityStat;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

// كل الأرقام هون عن الحالات المعالجة اللي سجّلناها نحن (مش عن حالات الدليل)
public class StatisticsFragment extends Fragment {

    private static final int DAYS_SHOWN = 10;

    private StatsRepository repository;
    private TextView treatedTotal;
    private TextView emptyView;
    private View content;
    private LinearLayout severityContainer;
    private LinearLayout caseContainer;
    private LinearLayout monthContainer;
    private LinearLayout dayContainer;

    public StatisticsFragment() {
        super(R.layout.fragment_statistics);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new StatsRepository(requireContext());
        treatedTotal = view.findViewById(R.id.statsTreatedTotal);
        emptyView = view.findViewById(R.id.statsEmpty);
        content = view.findViewById(R.id.statsContent);
        severityContainer = view.findViewById(R.id.statsSeverityContainer);
        caseContainer = view.findViewById(R.id.statsCaseContainer);
        monthContainer = view.findViewById(R.id.statsMonthContainer);
        dayContainer = view.findViewById(R.id.statsDayContainer);

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

        int total = repository.getTreatedTotal();
        treatedTotal.setText(String.valueOf(total));
        boolean empty = total == 0;
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        content.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) return;

        // ---- حسب الخطورة (نسبة من كل الحالات المعالجة) ----
        severityContainer.removeAllViews();
        for (SeverityStat stat : repository.getSeverityStats()) {
            addRow(severityContainer, inflater,
                    stat.getSeverity().iconRes,
                    getString(stat.getSeverity().nameRes),
                    stat.getCount(), total,
                    ContextCompat.getColor(requireContext(), stat.getSeverity().colorRes));
        }

        // ---- حسب اسم الحالة ----
        List<CaseStat> cases = repository.getCaseStats();
        int maxCase = 0;
        for (CaseStat stat : cases) maxCase = Math.max(maxCase, stat.getCount());
        caseContainer.removeAllViews();
        for (CaseStat stat : cases) {
            addRow(caseContainer, inflater,
                    stat.getSeverity().iconRes,
                    stat.getTitle(),
                    stat.getCount(), maxCase,
                    ContextCompat.getColor(requireContext(), stat.getSeverity().colorRes));
        }

        int primary = MaterialColors.getColor(monthContainer, androidx.appcompat.R.attr.colorPrimary);

        // ---- حسب الشهر ----
        List<MonthStat> months = repository.getTreatedPerMonth();
        int maxMonth = 0;
        for (MonthStat stat : months) maxMonth = Math.max(maxMonth, stat.getCount());
        monthContainer.removeAllViews();
        for (MonthStat stat : months) {
            addRow(monthContainer, inflater, 0,
                    formatMonth(stat.getMonth()), stat.getCount(), maxMonth, primary);
        }

        // ---- حسب اليوم (آخر أيام فيها تسجيل) ----
        List<DayStat> days = repository.getTreatedPerDay(DAYS_SHOWN);
        int maxDay = 0;
        for (DayStat stat : days) maxDay = Math.max(maxDay, stat.getCount());
        dayContainer.removeAllViews();
        for (DayStat stat : days) {
            addRow(dayContainer, inflater, 0,
                    formatDay(stat.getDate()), stat.getCount(), maxDay, primary);
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

    private static String formatDay(String iso) {
        try {
            return LocalDate.parse(iso).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        } catch (DateTimeParseException e) {
            return iso;
        }
    }
}
