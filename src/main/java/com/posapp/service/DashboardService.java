package com.posapp.service;

import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.csv.ProductDaoCsv;
import com.posapp.dao.csv.TransactionDaoCsv;
import com.posapp.model.Product;
import com.posapp.model.Transaction;
import com.posapp.model.enums.TransactionStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardService {
    private final TransactionDao transactionDao;
    private final ProductDao productDao;

    public DashboardService(TransactionDao transactionDao, ProductDao productDao) {
        this.transactionDao = transactionDao;
        this.productDao = productDao;
    }

    public DashboardService() {
        this(new TransactionDaoCsv(), new ProductDaoCsv());
    }

    public double getTodaySales() {
        LocalDate today = LocalDate.now();
        return transactionDao.findByDateRange(today, today).stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .mapToDouble(Transaction::getGrandTotal)
                .sum();
    }

    public int getTodayTransactionCount() {
        LocalDate today = LocalDate.now();
        return (int) transactionDao.findByDateRange(today, today).stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .count();
    }

    public int getTotalProductCount() {
        return productDao.findAll().size();
    }

    public int getLowStockCount() {
        return (int) productDao.findAll().stream()
                .filter(Product::isLowStock)
                .count();
    }

    public List<Product> getLowStockProducts() {
        return productDao.findAll().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }
}

