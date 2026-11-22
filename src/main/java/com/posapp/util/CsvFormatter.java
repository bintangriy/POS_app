package com.posapp.util;

import java.util.List;

/**
 * Formatter data ke format baris CSV standar RFC 4180.
 * Membungkus nilai dengan tanda kutip jika mengandung koma, tanda kutip, atau baris baru.
 */
public class CsvFormatter {

    private CsvFormatter() {}

    /**
     * Mengubah daftar nilai string menjadi satu baris teks CSV yang valid.
     *
     * @param fields daftar field
     * @return string baris CSV
     */
    public static String formatLine(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            sb.append(escapeField(field));
            if (i < fields.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Mengamankan field jika mengandung koma, quote, atau newline.
     */
    public static String escapeField(String field) {
        if (field == null) {
            return "";
        }

        boolean containsSpecialChar = field.contains(",") || field.contains("\"") || 
                                      field.contains("\n") || field.contains("\r");

        if (!containsSpecialChar) {
            return field;
        }

        // Karakter kutip diduplikasi: " -> ""
        String escaped = field.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}

