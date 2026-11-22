package com.posapp.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utilitas pemformatan nilai moneter ke format mata uang Rupiah.
 */
public class CurrencyFormatter {

    private static final DecimalFormat RUPIAH_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        RUPIAH_FORMAT = new DecimalFormat("Rp #,##0", symbols);
    }

    private CurrencyFormatter() {}

    public static String formatRupiah(double amount) {
        return RUPIAH_FORMAT.format(amount);
    }

    public static double parseRupiah(String text) {
        if (text == null) return 0;
        String clean = text.replace("Rp", "").replace(".", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

