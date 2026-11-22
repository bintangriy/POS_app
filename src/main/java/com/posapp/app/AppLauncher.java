package com.posapp.app;

/**
 * Main launcher wrapper class.
 * Diperlukan untuk peluncuran JavaFX mandiri tanpa isu module path pada Java 21+.
 */
public class AppLauncher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}

