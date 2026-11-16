package com.smartpantry.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import com.smartpantry.app.R;
import com.smartpantry.app.logic.DateUtils;
import com.smartpantry.app.logic.RecipeMatcher;
import com.smartpantry.app.model.Recipe;
import java.util.List;

public final class SuggestedRecipesActivity extends BaseActivity {
    private RecipeAdapter adapter;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        screen(R.layout.activity_suggested_recipes, R.string.suggested_recipes, false, R.id.navRecipes);
        adapter = new RecipeAdapter(this);
        ListView list = findViewById(R.id.recipeList);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.emptyRecipes));
        list.setOnItemClickListener((parent, row, position, id) -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, id);
            startActivity(intent);
        });
        findViewById(R.id.addFromRecipes).setOnClickListener(v ->
                startActivity(new Intent(this, IngredientFormActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        List<Recipe> all = database.getRecipes();
        List<Recipe> matches = RecipeMatcher.suggestions(all, database.getPantry(), DateUtils.today());
        adapter.submit(matches);
        ((TextView) findViewById(R.id.matchesCount)).setText(getString(R.string.matches_count, matches.size(), all.size()));
    }
}
