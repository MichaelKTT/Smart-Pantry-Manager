package com.smartpantry.app.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.smartpantry.app.R;
import com.smartpantry.app.logic.DateUtils;
import com.smartpantry.app.logic.RecipeMatcher;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.model.Requirement;

public final class RecipeDetailActivity extends BaseActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        screen(R.layout.activity_recipe_detail, R.string.recipe_detail, true, 0);
    }

    @Override protected void onResume() {
        super.onResume();
        Recipe recipe = database.getRecipe(getIntent().getLongExtra(EXTRA_RECIPE_ID, -1));
        if (recipe == null) { Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show(); finish(); return; }
        ((TextView) findViewById(R.id.detailName)).setText(recipe.name);
        ((TextView) findViewById(R.id.detailDescription)).setText(recipe.description);
        ((TextView) findViewById(R.id.detailMeta)).setText(getString(R.string.recipe_meta, recipe.minutes, recipe.requirements.size()));
        boolean canMake = RecipeMatcher.canMake(recipe, database.getPantry(), DateUtils.today());
        TextView status = findViewById(R.id.detailStatus);
        status.setText(canMake ? R.string.everything_ready : R.string.pantry_changed);
        status.setTextColor(getColor(canMake ? R.color.accent : R.color.danger));
        StringBuilder ingredients = new StringBuilder();
        for (Requirement item : recipe.requirements) {
            if (ingredients.length() > 0) ingredients.append("\n\n");
            ingredients.append("•  ").append(item.name).append("  ·  ").append(Ui.amount(item.quantity, item.unit));
        }
        ((TextView) findViewById(R.id.detailIngredients)).setText(ingredients.toString());
        ((TextView) findViewById(R.id.detailSteps)).setText(recipe.steps);
    }
}
