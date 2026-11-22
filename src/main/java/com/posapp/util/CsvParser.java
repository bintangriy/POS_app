package com.posapp.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser CSV mandiri yang aman dan mematuhi standar RFC 4180.
 * Menangani koma di dalam tanda kutip, karakter kutip ganda (""), dan nilai kosong.
 * Tidak menggunakan String.split(",") yang rapuh.
 */
public class CsvParser {

    private CsvParser() {}

    /**
     * Mem-parse satu baris teks CSV menjadi daftar field string.
     *
     * @param line baris teks CSV
     * @return List of String berisi nilai setiap kolom
     */
    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        if (line == null) {
            return result;
        }

        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        int length = line.length();

        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < length && line.charAt(i + 1) == '"') {
                    // Escaped quote: "" -> "
                    currentField.append('"');
                    i++; // lewati quote kedua
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Akhir dari sebuah field
                result.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        // Tambahkan field terakhir
        result.add(currentField.toString().trim());
        return result;
    }
}

