package com.smartpantry.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import com.smartpantry.app.R;
import com.smartpantry.app.data.DatabaseHelper;

public abstract class BaseActivity extends Activity {
    protected DatabaseHelper database;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = DatabaseHelper.getInstance(this);
    }

    protected void screen(int layout, int title, boolean back, int selectedTab) {
        setContentView(layout);
        ((TextView) findViewById(R.id.screenTitle)).setText(title);
        View backButton = findViewById(R.id.backButton);
        backButton.setVisibility(back ? View.VISIBLE : View.GONE);
        backButton.setOnClickListener(v -> finish());
        View root = findViewById(R.id.root);
        // Android 15 enforces edge-to-edge for targetSdk 35. Keep controls clear
        // of system bars, cutouts and the keyboard, including gesture navigation.
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
            root.requestApplyInsets();
        } else root.setFitsSystemWindows(true);

        tab(R.id.navPantry, PantryActivity.class, selectedTab == R.id.navPantry);
        tab(R.id.navRecipes, SuggestedRecipesActivity.class, selectedTab == R.id.navRecipes);
        tab(R.id.navSettings, SettingsActivity.class, selectedTab == R.id.navSettings);
    }

    private void tab(int viewId, Class<?> target, boolean selected) {
        TextView view = findViewById(viewId);
        if (view == null) return;
        view.setSelected(selected);
        view.setOnClickListener(v -> {
            if (getClass() == target) return;
            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            if (!(this instanceof PantryActivity)) finish();
        });
    }
}
