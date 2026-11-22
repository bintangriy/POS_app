package com.posapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    @Test
    @DisplayName("Mem-parse baris CSV standar dengan koma sederhana")
    void testStandardCsvLine() {
        String line = "1,89912345,Indomie Goreng,1,2500,3000,50,10";
        List<String> fields = CsvParser.parseLine(line);

        assertEquals(8, fields.size());
        assertEquals("1", fields.get(0));
        assertEquals("89912345", fields.get(1));
        assertEquals("Indomie Goreng", fields.get(2));
        assertEquals("3000", fields.get(5));
    }

    @Test
    @DisplayName("Mem-parse field yang mengandung tanda koma di dalam tanda kutip")
    void testFieldWithCommasInQuotes() {
        String line = "1,\"Beras, Ramos Super 5kg\",2,65000,74000,15,5";
        List<String> fields = CsvParser.parseLine(line);

        assertEquals(7, fields.size());
        assertEquals("Beras, Ramos Super 5kg", fields.get(1));
        assertEquals("74000", fields.get(4));
    }

    @Test
    @DisplayName("Mem-parse field dengan escaped quotes (\"\" -> \")")
    void testFieldWithEscapedQuotes() {
        String line = "6,\"Sabun \"\"Total 10\"\" Lifebuoy\",3,3500,4500,4,10";
        List<String> fields = CsvParser.parseLine(line);

        assertEquals(7, fields.size());
        assertEquals("Sabun \"Total 10\" Lifebuoy", fields.get(1));
    }

    @Test
    @DisplayName("Mem-parse baris dengan nilai kosong di antara koma")
    void testEmptyFields() {
        String line = "1,admin,$2a$10$hash,Admin Toko,ADMIN,,true";
        List<String> fields = CsvParser.parseLine(line);

        assertEquals(7, fields.size());
        assertEquals("admin", fields.get(1));
        assertEquals("", fields.get(5)); // terminal_id kosong
        assertEquals("true", fields.get(6));
    }
}

