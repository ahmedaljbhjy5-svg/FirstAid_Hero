package com.ahmed.firstaidhero1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface Listener {
        void onCall(Contact contact);
        void onEdit(Contact contact);
        void onDelete(Contact contact);
    }

    private final List<Contact> items = new ArrayList<>();
    private final Listener listener;

    public ContactAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Contact> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = items.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

        // الأرقام العامة ما بتتعدل ولا بتنحذف: نخفي الزرين، ونعرضهم لباقي الصفوف (بسبب إعادة التدوير)
        int visibility = contact.isBuiltIn() ? View.GONE : View.VISIBLE;
        holder.edit.setVisibility(visibility);
        holder.delete.setVisibility(visibility);

        holder.itemView.setOnClickListener(v -> listener.onCall(contact));
        holder.edit.setOnClickListener(v -> listener.onEdit(contact));
        holder.delete.setOnClickListener(v -> listener.onDelete(contact));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView phone;
        final ImageButton edit;
        final ImageButton delete;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            phone = itemView.findViewById(R.id.contactPhone);
            edit = itemView.findViewById(R.id.contactEdit);
            delete = itemView.findViewById(R.id.contactDelete);
        }
    }
}