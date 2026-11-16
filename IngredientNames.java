package com.smartpantry.app.logic;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Explicit food aliases avoid unsafe fuzzy matches (e.g. rice and rice flour). */
public final class IngredientNames {
    private static final Map<String, String> ALIASES = new HashMap<>();
    static {
        alias("tomato", "tomatoes");
        alias("potato", "potatoes");
        alias("egg", "eggs");
        alias("onion", "onions");
        alias("banana", "bananas");
        alias("carrot", "carrots");
        alias("apple", "apples");
        alias("chickpea", "chickpeas", "garbanzo beans", "garbanzo bean");
        alias("cooked chickpea", "cooked chickpeas", "canned chickpea", "canned chickpeas",
                "tinned chickpea", "tinned chickpeas");
        alias("oats", "oat", "rolled oats");
        alias("yogurt", "yoghurt", "plain yogurt", "plain yoghurt");
        alias("oil", "cooking oil", "vegetable oil", "sunflower oil");
        alias("garlic clove", "garlic cloves");
        alias("bread", "bread slice", "bread slices");
        alias("cabbage", "cabbages");
        alias("canned tuna", "tinned tuna");
    }

    private IngredientNames() { }

    private static void alias(String canonical, String... variants) {
        ALIASES.put(canonical, canonical);
        for (String variant : variants) ALIASES.put(variant, canonical);
    }

    public static String canonical(String name) {
        if (name == null) return "";
        String cleaned = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).replace('-', ' ').replace('_', ' ')
                .trim().replaceAll("\\s+", " ");
        return ALIASES.getOrDefault(cleaned, cleaned);
    }
}
