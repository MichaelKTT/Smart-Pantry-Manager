package com.smartpantry.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.smartpantry.app.R;
import com.smartpantry.app.model.Recipe;
import java.util.ArrayList;
import java.util.List;

public final class RecipeAdapter extends BaseAdapter {
    private final Context context;
    private final List<Recipe> recipes = new ArrayList<>();
    public RecipeAdapter(Context context) { this.context = context; }
    public void submit(List<Recipe> result) { recipes.clear(); recipes.addAll(result); notifyDataSetChanged(); }
    @Override public int getCount() { return recipes.size(); }
    @Override public Recipe getItem(int position) { return recipes.get(position); }
    @Override public long getItemId(int position) { return getItem(position).id; }
    @Override public boolean hasStableIds() { return true; }
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView == null ? LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false) : convertView;
        Recipe recipe = getItem(position);
        ((TextView) row.findViewById(R.id.recipeName)).setText(recipe.name);
        ((TextView) row.findViewById(R.id.recipeDescription)).setText(recipe.description);
        ((TextView) row.findViewById(R.id.recipeMeta)).setText(context.getString(R.string.recipe_meta,
                recipe.minutes, recipe.requirements.size()));
        return row;
    }
}
