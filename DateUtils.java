package com.smartpantry.app.logic;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class DateUtils {
    private DateUtils() { }

    public static boolean valid(String value) {
        if (value == null || value.isEmpty()) return true;
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) return false;
        try { LocalDate.parse(value); return true; }
        catch (DateTimeParseException error) { return false; }
    }

    public static String today() { return LocalDate.now().toString(); }

    public static boolean expired(String expiry, String today) {
        return expiry != null && !expiry.isEmpty() && expiry.compareTo(today) < 0;
    }

    public static boolean soon(String expiry, String today) {
        return expiry != null && !expiry.isEmpty() && !expired(expiry, today)
                && expiry.compareTo(LocalDate.parse(today).plusDays(3).toString()) <= 0;
    }
}
