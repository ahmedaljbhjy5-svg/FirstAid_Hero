package com.ahmed.firstaidhero1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.CardItem;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CardItem item);
    }

    private final List<CardItem> items;
    private final OnItemClickListener listener;

    public CardAdapter(List<CardItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // 1) بيتنفذ فقط لما يحتاج RecyclerView كرت جديد (عدد قليل من المرات)
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    // 2) بيتنفذ مع كل كرت بيظهر: بيعبي الكرت (القديم المُعاد تدويره) ببيانات جديدة
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());
        holder.image.setImageResource(item.getImageRes());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    // 3) كم عنصر عندي بالمجموع
    @Override
    public int getItemCount() {
        return items.size();
    }

    // بيحفظ مراجع الـ Views مرة وحدة
    static class CardViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView subtitle;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cardImage);
            title = itemView.findViewById(R.id.cardTitle);
            subtitle = itemView.findViewById(R.id.cardSubtitle);
        }
    }
}