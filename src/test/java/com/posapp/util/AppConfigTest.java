package com.posapp.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @AfterEach
    void tearDown() {
        AppConfig.reset();
    }

    @Test
    @DisplayName("Membaca konfigurasi yang valid via FileInputStream")
    void testLoadValidConfig(@TempDir File tempDir) throws IOException {
        File configFile = new File(tempDir, "test.properties");
        try (FileWriter fw = new FileWriter(configFile)) {
            fw.write("app.data.dir=" + tempDir.getAbsolutePath() + "\n");
            fw.write("app.name=POS_app_Test\n");
        }

        AppConfig.setConfigPath(configFile.getAbsolutePath());
        AppConfig.load();

        assertEquals(tempDir.getAbsolutePath(), AppConfig.getDataDir());
        assertEquals("POS_app_Test", AppConfig.get("app.name"));
    }

    @Test
    @DisplayName("Menangani file konfigurasi yang tidak ditemukan dengan pesan jelas")
    void testFileNotFound() {
        AppConfig.setConfigPath("non_existent_config.properties");
        RuntimeException ex = assertThrows(RuntimeException.class, AppConfig::load);
        assertTrue(ex.getMessage().contains("tidak ditemukan"));
    }
}

