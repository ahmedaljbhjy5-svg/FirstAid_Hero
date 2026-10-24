package com.ahmed.firstaidhero1.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.firstaidhero1.R;
import com.ahmed.firstaidhero1.adapter.ContactAdapter;
import com.ahmed.firstaidhero1.data.ContactRepository;
import com.ahmed.firstaidhero1.model.Contact;
import com.ahmed.firstaidhero1.util.DialHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ContactsFragment extends Fragment {

    private ContactRepository repository;
    private ContactAdapter adapter;
    private TextView emptyHint;

    public ContactsFragment() {
        super(R.layout.fragment_contacts);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new ContactRepository(requireContext());
        emptyHint = view.findViewById(R.id.contactsEmpty);

        adapter = new ContactAdapter(new ContactAdapter.Listener() {
            @Override
            public void onCall(Contact contact) {
                DialHelper.dial(requireContext(), contact.getPhone());
            }

            @Override
            public void onDelete(Contact contact) {
                confirmDelete(contact);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.contactsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addContactFab).setOnClickListener(v -> showAddDialog());

        reload();
    }

    // نقرأ من القاعدة ونحدّث الشاشة
    private void reload() {
        List<Contact> contacts = repository.getAll();
        adapter.setItems(contacts);
        // لو ما في غير صف الإسعاف، منعرض رسالة الإرشاد
        emptyHint.setVisibility(contacts.size() <= 1 ? View.VISIBLE : View.GONE);
    }

    private void showAddDialog() {
        View form = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_contact, null);
        TextInputLayout nameLayout = form.findViewById(R.id.nameLayout);
        TextInputEditText nameInput = form.findViewById(R.id.nameInput);
        TextInputLayout phoneLayout = form.findViewById(R.id.phoneLayout);
        TextInputEditText phoneInput = form.findViewById(R.id.phoneInput);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.contact_add_title)
                .setView(form)
                .setPositiveButton(R.string.save, null) // null: بنعرّف الضغط بعد show()
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = textOf(nameInput);
            String phone = textOf(phoneInput);

            boolean valid = true;
            if (name.isEmpty()) {
                nameLayout.setError(getString(R.string.contact_name_required));
                valid = false;
            } else {
                nameLayout.setError(null);
            }
            if (!isValidPhone(phone)) {
                phoneLayout.setError(getString(R.string.contact_phone_invalid));
                valid = false;
            } else {
                phoneLayout.setError(null);
            }
            if (!valid) return; // الحوار يضل مفتوح ويعرض الأخطاء

            repository.insert(name, phone);
            dialog.dismiss();
            reload();
        });
    }

    private void confirmDelete(Contact contact) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.contact_delete_title)
                .setMessage(getString(R.string.contact_delete_message, contact.getName()))
                .setPositiveButton(R.string.delete, (d, which) -> {
                    repository.delete(contact.getId());
                    reload();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private static String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    // أرقام وأقواس و+ و- ومسافات فقط، وما يقل عن 3 أرقام
    private static boolean isValidPhone(String phone) {
        return phone.matches("[0-9+()\\-\\s]+")
                && phone.replaceAll("[^0-9]", "").length() >= 3;
    }
}