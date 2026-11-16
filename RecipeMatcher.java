package com.smartpantry.app.logic;

import com.smartpantry.app.model.Ingredient;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.model.Requirement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pure Java business logic: no Android dependency, easy to explain and test. */
public final class RecipeMatcher {
    private RecipeMatcher() { }

    public static boolean canMake(Recipe recipe, List<Ingredient> pantry, String today) {
        if (recipe.requirements.isEmpty()) return false;
        Map<String, Double> available = new HashMap<>();
        for (Ingredient item : pantry) {
            if (!Double.isFinite(item.quantity) || item.quantity <= 0
                    || !DateUtils.valid(item.expiryDate)
                    || DateUtils.expired(item.expiryDate, today)) continue;
            String key = key(item.name, item.unit);
            if (key == null) continue;
            available.put(key, available.getOrDefault(key, 0.0)
                    + UnitConverter.toBase(item.quantity, item.unit));
        }

        // Aggregate recipe lines as well: repeated ingredients cannot reuse stock.
        Map<String, Double> required = new HashMap<>();
        for (Requirement item : recipe.requirements) {
            String key = key(item.name, item.unit);
            if (key == null || !Double.isFinite(item.quantity) || item.quantity <= 0) return false;
            required.put(key, required.getOrDefault(key, 0.0)
                    + UnitConverter.toBase(item.quantity, item.unit));
        }
        for (Map.Entry<String, Double> item : required.entrySet()) {
            // A very small tolerance only absorbs floating-point roundoff.
            if (available.getOrDefault(item.getKey(), 0.0) + 1e-9 < item.getValue()) return false;
        }
        return true;
    }

    public static List<Recipe> suggestions(List<Recipe> recipes, List<Ingredient> pantry, String today) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) if (canMake(recipe, pantry, today)) result.add(recipe);
        return result;
    }

    private static String key(String name, String unit) {
        String ingredient = IngredientNames.canonical(name);
        String dimension = UnitConverter.dimension(unit);
        return ingredient.isEmpty() || dimension.isEmpty() ? null : ingredient + "|" + dimension;
    }
}
