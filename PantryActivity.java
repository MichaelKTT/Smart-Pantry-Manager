package com.smartpantry.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.smartpantry.app.R;
import com.smartpantry.app.logic.DateUtils;
import com.smartpantry.app.logic.RecipeMatcher;
import com.smartpantry.app.model.Ingredient;
import java.util.List;

public final class PantryActivity extends BaseActivity implements PantryAdapter.Actions {
    private PantryAdapter adapter;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        screen(R.layout.activity_pantry, R.string.your_pantry, false, R.id.navPantry);
        adapter = new PantryAdapter(this, this);
        ListView list = findViewById(R.id.pantryList);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.emptyPantry));
        findViewById(R.id.addIngredient).setOnClickListener(v ->
                startActivity(new Intent(this, IngredientFormActivity.class)));
        findViewById(R.id.findRecipes).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        // Reload after add/edit, navigation and reopening the app.
        refreshPantry();
    }

    private void refreshPantry() {
        List<Ingredient> pantry = database.getPantry();
        boolean hints = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("expiry_hints", true);
        adapter.submit(pantry, hints);
        int matches = RecipeMatcher.suggestions(database.getRecipes(), pantry, DateUtils.today()).size();
        ((TextView) findViewById(R.id.pantryCount)).setText(String.valueOf(pantry.size()));
        ((TextView) findViewById(R.id.readyCount)).setText(String.valueOf(matches));
        int soon = 0;
        for (Ingredient item : pantry) if (DateUtils.soon(item.expiryDate, DateUtils.today())) soon++;
        ((TextView) findViewById(R.id.pantryHint)).setText(hints && soon > 0
                ? getString(R.string.use_soon_count, soon) : getString(R.string.pantry_tip));
    }

    @Override public void edit(Ingredient item) {
        Intent intent = new Intent(this, IngredientFormActivity.class);
        intent.putExtra(IngredientFormActivity.EXTRA_ITEM_ID, item.id);
        startActivity(intent);
    }

    @Override public void delete(Ingredient item) {
        new AlertDialog.Builder(this).setTitle(R.string.delete_ingredient)
                .setMessage(getString(R.string.delete_question, item.name))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    database.deleteIngredient(item.id);
                    Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                    refreshPantry();
                }).show();
    }

}
