package com.ahmed.firstaidhero1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.CardItem;

public class CardAdapter extends ListAdapter<CardItem, CardAdapter.CardViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CardItem item);
    }

    // بتحدد شو تغيّر بين قائمتين، فالتحديث بيحرّك بس الصفوف المتأثرة
    private static final DiffUtil.ItemCallback<CardItem> DIFF = new DiffUtil.ItemCallback<CardItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CardItem a, @NonNull CardItem b) {
            return a.getId() == b.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CardItem a, @NonNull CardItem b) {
            return a.getTitle().equals(b.getTitle())
                    && a.getDescription().equals(b.getDescription())
                    && a.getSeverity() == b.getSeverity();
        }
    };

    private final OnItemClickListener listener;

    public CardAdapter(OnItemClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem item = getItem(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.image.setImageResource(item.getImageRes());
        holder.severity.setImageResource(item.getSeverity().iconRes);
        holder.severity.setContentDescription(
                holder.itemView.getContext().getString(item.getSeverity().labelRes));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final ImageView severity;
        final TextView title;
        final TextView description;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cardImage);
            severity = itemView.findViewById(R.id.cardSeverity);
            title = itemView.findViewById(R.id.cardTitle);
            description = itemView.findViewById(R.id.cardDescription);
        }
    }
}