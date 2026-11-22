package com.posapp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Manajer operasi Java I/O untuk file CSV.
 * Menggunakan File, FileReader, BufferedReader, FileWriter, BufferedWriter, dan IOException
 * dengan mekanisme atomic file swap (.tmp) dan try-with-resources.
 */
public class CsvFileManager {

    private final String baseDirectory;

    public CsvFileManager(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        ensureDirectoryExists();
    }

    public CsvFileManager() {
        this(AppConfig.getDataDir());
    }

    private void ensureDirectoryExists() {
        File dir = new File(baseDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public File getFile(String fileName) {
        return new File(baseDirectory, fileName);
    }

    /**
     * Membaca seluruh baris dari file CSV.
     * Menggunakan FileReader dan BufferedReader dalam try-with-resources.
     *
     * @param fileName nama berkas CSV (misal: "products.csv")
     * @return List of List of String (setiap baris terurai menjadi list field)
     * @throws IOException jika terjadi kegagalan I/O
     */
    public synchronized List<List<String>> readAll(String fileName) throws IOException {
        File file = getFile(fileName);
        List<List<String>> rows = new ArrayList<>();

        if (!file.exists()) {
            return rows;
        }

        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    rows.add(CsvParser.parseLine(line));
                }
            }
        }
        return rows;
    }

    /**
     * Menulis seluruh baris ke file CSV secara atomik menggunakan file sementara (.tmp).
     * Menggunakan FileWriter dan BufferedWriter dalam try-with-resources.
     *
     * @param fileName nama berkas CSV
     * @param header baris header kolom
     * @param records baris-baris data
     * @throws IOException jika penulisan gagal
     */
    public synchronized void writeAll(String fileName, List<String> header, List<List<String>> records) throws IOException {
        ensureDirectoryExists();
        File targetFile = getFile(fileName);
        File tempFile = new File(baseDirectory, fileName + ".tmp");

        try (FileWriter fw = new FileWriter(tempFile);
             BufferedWriter writer = new BufferedWriter(fw)) {
            
            // Tulis header
            if (header != null && !header.isEmpty()) {
                writer.write(CsvFormatter.formatLine(header));
                writer.newLine();
            }

            // Tulis records
            if (records != null) {
                for (List<String> record : records) {
                    writer.write(CsvFormatter.formatLine(record));
                    writer.newLine();
                }
            }
            writer.flush();
        }

        // Penggantian file lama secara atomik
        try {
            Files.move(tempFile.toPath(), targetFile.toPath(), 
                       StandardCopyOption.ATOMIC_MOVE, 
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Fallback jika filesystem tidak mendukung ATOMIC_MOVE
            Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Menambahkan satu baris data ke akhir file CSV (Append mode).
     *
     * @param fileName nama berkas CSV
     * @param record data yang akan ditambahkan
     * @throws IOException jika gagal menulis
     */
    public synchronized void append(String fileName, List<String> record) throws IOException {
        ensureDirectoryExists();
        File targetFile = getFile(fileName);
        boolean fileExists = targetFile.exists() && targetFile.length() > 0;

        try (FileWriter fw = new FileWriter(targetFile, true);
             BufferedWriter writer = new BufferedWriter(fw)) {
            
            writer.write(CsvFormatter.formatLine(record));
            writer.newLine();
            writer.flush();
        }
    }

    /**
     * Memastikan file CSV ada beserta headernya.
     */
    public synchronized void ensureFileWithHeader(String fileName, List<String> defaultHeader) throws IOException {
        ensureDirectoryExists();
        File file = getFile(fileName);
        if (!file.exists() || file.length() == 0) {
            writeAll(fileName, defaultHeader, new ArrayList<>());
        }
    }
}

