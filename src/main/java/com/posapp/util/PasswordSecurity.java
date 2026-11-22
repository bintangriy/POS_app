package com.posapp.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitas enkripsi dan verifikasi kata sandi menggunakan BCrypt.
 * Mencegah penyimpanan kata sandi dalam bentuk plaintext.
 */
public class PasswordSecurity {

    private PasswordSecurity() {}

    /**
     * Menghasilkan hash BCrypt dari password mentah.
     *
     * @param plainPassword kata sandi plaintext
     * @return hash password terenkripsi
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /**
     * Memverifikasi kecocokan password mentah terhadap hash yang tersimpan.
     *
     * @param plainPassword kata sandi yang diinput pengguna
     * @param hashedPassword hash password yang tersimpan di sistem
     * @return true jika cocok, false jika tidak cocok
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

