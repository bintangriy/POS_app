package com.posapp.dao.csv;

import com.posapp.dao.InventoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.dao.UserDao;
import com.posapp.model.Product;
import com.posapp.model.StockMovement;
import com.posapp.model.User;
import com.posapp.model.enums.MovementType;
import com.posapp.util.CsvFileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryDaoCsv implements InventoryDao {
    private static final String FILE_NAME = "stock_movements.csv";
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final List<String> HEADER = Arrays.asList(
        "id", "product_id", "user_id", "movement_type", "quantity", 
        "stock_before", "stock_after", "remarks", "created_at"
    );

    private final CsvFileManager fileManager;
    private final ProductDao productDao;
    private final UserDao userDao;

    public InventoryDaoCsv(CsvFileManager fileManager, ProductDao productDao, UserDao userDao) {
        this.fileManager = fileManager;
        this.productDao = productDao;
        this.userDao = userDao;
        try {
            this.fileManager.ensureFileWithHeader(FILE_NAME, HEADER);
        } catch (IOException e) {
            throw new RuntimeException("Gagal inisialisasi file " + FILE_NAME, e);
        }
    }

    public InventoryDaoCsv() {
        this(new CsvFileManager(), new ProductDaoCsv(), new UserDaoCsv());
    }

    @Override
    public synchronized boolean insertMovement(StockMovement movement) {
        try {
            List<List<String>> rows = fileManager.readAll(FILE_NAME);
            int newId = 1;
            for (int i = 1; i < rows.size(); i++) {
                int existingId = Integer.parseInt(rows.get(i).get(0));
                if (existingId >= newId) {
                    newId = existingId + 1;
                }
            }
            movement.setId(newId);

            List<String> record = Arrays.asList(
                String.valueOf(movement.getId()),
                String.valueOf(movement.getProduct().getId()),
                String.valueOf(movement.getUser() != null ? movement.getUser().getId() : 0),
                movement.getMovementType().name(),
                String.valueOf(movement.getQuantity()),
                String.valueOf(movement.getStockBefore()),
                String.valueOf(movement.getStockAfter()),
                movement.getRemarks() != null ? movement.getRemarks() : "",
                movement.getCreatedAt().format(ISO_FMT)
            );

            fileManager.append(FILE_NAME, record);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Gagal mencatat mutasi stok: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StockMovement> findAllMovements() {
        List<StockMovement> list = new ArrayList<>();
        try {
            List<List<String>> rows = fileManager.readAll(FILE_NAME);
            for (int i = 1; i < rows.size(); i++) {
                List<String> r = rows.get(i);
                if (r.size() >= 9) {
                    int id = Integer.parseInt(r.get(0));
                    int prodId = Integer.parseInt(r.get(1));
                    int userId = Integer.parseInt(r.get(2));
                    MovementType type = MovementType.valueOf(r.get(3).toUpperCase());
                    int qty = Integer.parseInt(r.get(4));
                    int before = Integer.parseInt(r.get(5));
                    int after = Integer.parseInt(r.get(6));
                    String remarks = r.get(7);
                    LocalDateTime time = LocalDateTime.parse(r.get(8), ISO_FMT);

                    Product prod = productDao.findById(prodId);
                    User user = userDao.findById(userId);

                    list.add(new StockMovement(id, prod, user, type, qty, before, after, remarks, time));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca mutasi stok: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<StockMovement> findMovementsByProductId(int productId) {
        return findAllMovements().stream()
                .filter(m -> m.getProduct() != null && m.getProduct().getId() == productId)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovement> findMovementsByDateRange(LocalDate from, LocalDate to) {
        return findAllMovements().stream().filter(m -> {
            LocalDate mDate = m.getCreatedAt().toLocalDate();
            return (from == null || !mDate.isBefore(from)) && (to == null || !mDate.isAfter(to));
        }).collect(Collectors.toList());
    }
}

