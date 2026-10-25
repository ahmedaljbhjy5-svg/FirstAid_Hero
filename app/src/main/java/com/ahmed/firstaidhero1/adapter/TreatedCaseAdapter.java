package com.ahmed.firstaidhero1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.TreatedCase;
import com.ahmed.firstaidhero1.util.PhotoStorage;
import com.bumptech.glide.Glide;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Objects;

public class TreatedCaseAdapter
        extends ListAdapter<TreatedCase, TreatedCaseAdapter.TreatedViewHolder> {

    public interface OnDeleteListener {
        void onDelete(TreatedCase treatedCase);
    }

    private static final DiffUtil.ItemCallback<TreatedCase> DIFF =
            new DiffUtil.ItemCallback<TreatedCase>() {
                @Override
                public boolean areItemsTheSame(@NonNull TreatedCase a, @NonNull TreatedCase b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TreatedCase a, @NonNull TreatedCase b) {
                    return a.getDescription().equals(b.getDescription())
                            && a.getProcedures().equals(b.getProcedures())
                            && a.getDate().equals(b.getDate())
                            && Objects.equals(a.getPhotoPath(), b.getPhotoPath());
                }
            };

    private final OnDeleteListener listener;

    public TreatedCaseAdapter(OnDeleteListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TreatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_treated_case, parent, false);
        return new TreatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreatedViewHolder holder, int position) {
        TreatedCase item = getItem(position);
        holder.date.setText(formatDate(item.getDate()));
        holder.description.setText(item.getDescription());
        holder.procedures.setText(item.getProcedures());

        // لازم نعالج الحالتين (بصورة وبدون) بكل bind بسبب إعادة التدوير
        if (item.getPhotoPath() == null) {
            Glide.with(holder.photo).clear(holder.photo);
            holder.photo.setImageResource(R.drawable.ic_photo_placeholder);
        } else {
            Glide.with(holder.photo)
                    .load(PhotoStorage.resolve(holder.itemView.getContext(), item.getPhotoPath()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo_placeholder)
                    .error(R.drawable.ic_photo_placeholder) // الملف انحذف أو تالف
                    .into(holder.photo);
        }

        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    private static String formatDate(String iso) {
        try {
            return LocalDate.parse(iso).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        } catch (DateTimeParseException e) {
            return iso; // صيغة غير متوقعة: نعرضها كما هي بدل ما ننهار
        }
    }

    static class TreatedViewHolder extends RecyclerView.ViewHolder {
        final ImageView photo;
        final TextView date;
        final TextView description;
        final TextView procedures;
        final ImageButton delete;

        TreatedViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.treatedPhoto);
            date = itemView.findViewById(R.id.treatedDate);
            description = itemView.findViewById(R.id.treatedDescription);
            procedures = itemView.findViewById(R.id.treatedProcedures);
            delete = itemView.findViewById(R.id.treatedDelete);
        }
    }
}