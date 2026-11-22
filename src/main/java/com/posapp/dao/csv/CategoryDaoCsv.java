package com.posapp.dao.csv;

import com.posapp.dao.CategoryDao;
import com.posapp.model.Category;
import com.posapp.util.CsvFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryDaoCsv implements CategoryDao {
    private static final String FILE_NAME = "categories.csv";
    private static final List<String> HEADER = Arrays.asList("id", "name", "description");

    private final CsvFileManager fileManager;

    public CategoryDaoCsv(CsvFileManager fileManager) {
        this.fileManager = fileManager;
        try {
            this.fileManager.ensureFileWithHeader(FILE_NAME, HEADER);
        } catch (IOException e) {
            throw new RuntimeException("Gagal inisialisasi file " + FILE_NAME, e);
        }
    }

    public CategoryDaoCsv() {
        this(new CsvFileManager());
    }

    @Override
    public Category findById(int id) {
        return findAll().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Category findByName(String name) {
        if (name == null) return null;
        return findAll().stream()
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .findFirst().orElse(null);
    }

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        try {
            List<List<String>> rows = fileManager.readAll(FILE_NAME);
            for (int i = 1; i < rows.size(); i++) {
                List<String> r = rows.get(i);
                if (r.size() >= 2) {
                    int id = Integer.parseInt(r.get(0));
                    String name = r.get(1);
                    String desc = r.size() > 2 ? r.get(2) : "";
                    list.add(new Category(id, name, desc));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca kategori: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean insert(Category category) {
        List<Category> all = findAll();
        int newId = all.stream().mapToInt(Category::getId).max().orElse(0) + 1;
        category.setId(newId);
        all.add(category);
        return saveAll(all);
    }

    @Override
    public boolean update(Category category) {
        List<Category> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == category.getId()) {
                all.set(i, category);
                found = true;
                break;
            }
        }
        return found && saveAll(all);
    }

    @Override
    public boolean delete(int id) {
        List<Category> all = findAll();
        boolean removed = all.removeIf(c -> c.getId() == id);
        return removed && saveAll(all);
    }

    private boolean saveAll(List<Category> categories) {
        List<List<String>> records = new ArrayList<>();
        for (Category c : categories) {
            records.add(Arrays.asList(
                String.valueOf(c.getId()),
                c.getName(),
                c.getDescription() != null ? c.getDescription() : ""
            ));
        }
        try {
            fileManager.writeAll(FILE_NAME, HEADER, records);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan kategori: " + e.getMessage(), e);
        }
    }
}

