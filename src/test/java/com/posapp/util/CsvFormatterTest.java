package com.posapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvFormatterTest {

    @Test
    @DisplayName("Memformat baris CSV tanpa karakter khusus")
    void testFormatSimple() {
        List<String> fields = Arrays.asList("1", "Indomie", "3000");
        String formatted = CsvFormatter.formatLine(fields);
        assertEquals("1,Indomie,3000", formatted);
    }

    @Test
    @DisplayName("Meng-escape field yang memiliki koma dengan tanda kutip ganda")
    void testFormatWithComma() {
        List<String> fields = Arrays.asList("1", "Beras, Ramos Super", "75000");
        String formatted = CsvFormatter.formatLine(fields);
        assertEquals("1,\"Beras, Ramos Super\",75000", formatted);
    }

    @Test
    @DisplayName("Meng-escape field yang memiliki karakter kutip ganda (\" -> \"\")")
    void testFormatWithQuotes() {
        List<String> fields = Arrays.asList("1", "Sabun \"Lifebuoy\"", "4500");
        String formatted = CsvFormatter.formatLine(fields);
        assertEquals("1,\"Sabun \"\"Lifebuoy\"\"\",4500", formatted);
    }
}

