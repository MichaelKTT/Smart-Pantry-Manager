package com.smartpantry.app.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class Ui {
    private Ui() { }
    public static String amount(double value, String unit) {
        DecimalFormat format = new DecimalFormat("0.###", DecimalFormatSymbols.getInstance(Locale.US));
        return format.format(value) + " " + unit;
    }
}
