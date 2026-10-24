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
import com.ahmed.firstaidhero1.util.PhoneValidator;
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
            public void onEdit(Contact contact) {
                showContactDialog(contact);
            }

            @Override
            public void onDelete(Contact contact) {
                confirmDelete(contact);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.contactsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addContactFab).setOnClickListener(v -> showContactDialog(null));

        reload();
    }

    private void reload() {
        List<Contact> contacts = repository.getAll();
        adapter.setItems(contacts);

        boolean hasUserContacts = false;
        for (Contact c : contacts) {
            if (!c.isBuiltIn()) {
                hasUserContacts = true;
                break;
            }
        }
        emptyHint.setVisibility(hasUserContacts ? View.GONE : View.VISIBLE);
    }

    // نفس الحوار للإضافة (existing = null) وللتعديل (existing = الرقم الحالي)
    private void showContactDialog(@Nullable Contact existing) {
        View form = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_contact, null);
        TextInputLayout nameLayout = form.findViewById(R.id.nameLayout);
        TextInputEditText nameInput = form.findViewById(R.id.nameInput);
        TextInputLayout phoneLayout = form.findViewById(R.id.phoneLayout);
        TextInputEditText phoneInput = form.findViewById(R.id.phoneInput);

        if (existing != null) {
            nameInput.setText(existing.getName());
            phoneInput.setText(existing.getPhone());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(existing == null ? R.string.contact_add_title : R.string.contact_edit_title)
                .setView(form)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = textOf(nameInput).replaceAll("\\s+", " ");
            String phone = textOf(phoneInput);
            long editingId = existing == null ? -1 : existing.getId();

            nameLayout.setError(validateName(name));
            phoneLayout.setError(validatePhone(phone, editingId));
            if (nameLayout.getError() != null || phoneLayout.getError() != null) {
                return; // الحوار يضل مفتوح ويعرض الأخطاء
            }

            if (existing == null) {
                repository.insert(name, phone);
            } else {
                repository.update(existing.getId(), name, phone);
            }
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

    // كل دالة تحقق بترجع نص الخطأ، أو null إذا المدخل سليم
    @Nullable
    private String validateName(String name) {
        return name.isEmpty() ? getString(R.string.contact_name_required) : null;
    }

    @Nullable
    private String validatePhone(String phone, long excludeId) {
        if (!PhoneValidator.isValid(phone)) return getString(R.string.contact_phone_invalid);
        if (repository.phoneExists(phone, excludeId)) return getString(R.string.contact_phone_duplicate);
        return null;
    }

    private static String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}