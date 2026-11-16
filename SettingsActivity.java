package com.smartpantry.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;
import com.smartpantry.app.R;

public final class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        screen(R.layout.activity_settings, R.string.settings, false, R.id.navSettings);
        Switch expiry = findViewById(R.id.expirySwitch);
        expiry.setChecked(getSharedPreferences("settings", MODE_PRIVATE).getBoolean("expiry_hints", true));
        expiry.setOnCheckedChangeListener((button, checked) -> getSharedPreferences("settings", MODE_PRIVATE)
                .edit().putBoolean("expiry_hints", checked).apply());
        findViewById(R.id.loadDemo).setOnClickListener(v -> {
            if (!database.getPantry().isEmpty()) {
                Toast.makeText(this, R.string.demo_empty_only, Toast.LENGTH_LONG).show(); return;
            }
            new AlertDialog.Builder(this).setTitle(R.string.load_demo)
                    .setMessage(R.string.demo_explanation).setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.load_demo, (dialog, which) -> {
                        boolean loaded = database.loadDemoPantry();
                        Toast.makeText(this, loaded ? R.string.demo_loaded : R.string.demo_empty_only, Toast.LENGTH_LONG).show();
                    }).show();
        });
        findViewById(R.id.clearPantry).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.clear_pantry).setMessage(R.string.clear_warning)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.clear_pantry, (dialog, which) -> {
                    database.clearPantry(); Toast.makeText(this, R.string.pantry_cleared, Toast.LENGTH_SHORT).show();
                }).show());
    }
}
