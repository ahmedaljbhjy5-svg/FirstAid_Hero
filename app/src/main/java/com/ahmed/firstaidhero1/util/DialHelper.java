package com.ahmed.firstaidhero1.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.ahmed.firstaidhero1.R;

public final class DialHelper {

    private DialHelper() {}

    public static void dialEmergency(Context context) {
        String number = context.getString(R.string.emergency_number);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.no_dialer, Toast.LENGTH_SHORT).show();
        }
    }
}