package com.posapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Pengelola konfigurasi aplikasi berbasis Java I/O.
 * Membaca file eksternal 'config/app.properties' menggunakan java.io.FileInputStream.
 */
public class AppConfig {
    private static final String DEFAULT_CONFIG_PATH = "config/app.properties";
    private static final Properties properties = new Properties();
    private static boolean loaded = false;
    private static String activeConfigPath = DEFAULT_CONFIG_PATH;

    private AppConfig() {}

    public static synchronized void setConfigPath(String path) {
        activeConfigPath = path;
        loaded = false;
    }

    public static synchronized void load() {
        if (loaded) return;

        File configFile = new File(activeConfigPath);
        if (!configFile.exists()) {
            throw new RuntimeException(
                "File konfigurasi '" + activeConfigPath + "' tidak ditemukan!\n" +
                "Silakan salin 'config/app.properties.example' menjadi '" + activeConfigPath + "' " +
                "untuk menjalankan aplikasi."
            );
        }

        // Penggunaan eksplisit FileInputStream dengan try-with-resources
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            loaded = true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Gagal membuka file konfigurasi: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Kesalahan I/O saat membaca file konfigurasi: " + e.getMessage(), e);
        }
    }

    public static String get(String key) {
        if (!loaded) load();
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        if (!loaded) load();
        return properties.getProperty(key, defaultValue);
    }

    public static String getDataDir() {
        return get("app.data.dir", "data");
    }

    public static void reset() {
        properties.clear();
        loaded = false;
        activeConfigPath = DEFAULT_CONFIG_PATH;
    }
}

