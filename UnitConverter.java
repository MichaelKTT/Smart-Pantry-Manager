package com.smartpantry.app.logic;

import java.util.Locale;

/** Mass, volume, pieces and slices are deliberately separate dimensions. */
public final class UnitConverter {
    public static final String[] UNITS = {"g", "kg", "ml", "l", "piece", "slice", "tsp", "tbsp", "cup"};

    private UnitConverter() { }

    public static String normalize(String unit) {
        if (unit == null) return "";
        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "g": case "gram": case "grams": return "g";
            case "kg": case "kilogram": case "kilograms": return "kg";
            case "ml": case "millilitre": case "millilitres":
            case "milliliter": case "milliliters": return "ml";
            case "l": case "litre": case "litres": case "liter": case "liters": return "l";
            case "piece": case "pieces": case "pc": case "pcs": return "piece";
            case "slice": case "slices": return "slice";
            case "tsp": case "teaspoon": case "teaspoons": return "tsp";
            case "tbsp": case "tablespoon": case "tablespoons": return "tbsp";
            case "cup": case "cups": return "cup";
            default: return "";
        }
    }

    public static String dimension(String unit) {
        switch (normalize(unit)) {
            case "g": case "kg": return "mass";
            case "ml": case "l": case "tsp": case "tbsp": case "cup": return "volume";
            case "piece": return "piece";
            case "slice": return "slice";
            default: return "";
        }
    }

    public static double toBase(double quantity, String unit) {
        switch (normalize(unit)) {
            case "g": case "ml": case "piece": case "slice": return quantity;
            case "kg": case "l": return quantity * 1000.0;
            case "tsp": return quantity * 5.0;
            case "tbsp": return quantity * 15.0;
            case "cup": return quantity * 250.0;
            default: return Double.NaN;
        }
    }
}
