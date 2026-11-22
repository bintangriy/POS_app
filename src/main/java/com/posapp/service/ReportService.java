package com.posapp.service;

import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.csv.ProductDaoCsv;
import com.posapp.dao.csv.TransactionDaoCsv;
import com.posapp.model.Product;
import com.posapp.model.Transaction;
import com.posapp.model.TransactionItem;
import com.posapp.model.enums.TransactionStatus;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final TransactionDao transactionDao;
    private final ProductDao productDao;

    public ReportService(TransactionDao transactionDao, ProductDao productDao) {
        this.transactionDao = transactionDao;
        this.productDao = productDao;
    }

    public ReportService() {
        this(new TransactionDaoCsv(), new ProductDaoCsv());
    }

    /**
     * Rekapitulasi penjualan harian (omset, HPP, laba kotor, jumlah transaksi).
     */
    public Map<String, Object> getDailySalesReport(LocalDate date) {
        List<Transaction> list = transactionDao.findByDateRange(date, date).stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .collect(Collectors.toList());

        double totalOmset = 0;
        double totalHpp = 0;

        for (Transaction t : list) {
            totalOmset += t.getGrandTotal();
            for (TransactionItem item : t.getItems()) {
                totalHpp += (item.getBuyPriceSnapshot() * item.getQuantity());
            }
        }

        double grossProfit = totalOmset - totalHpp;

        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("transactionCount", list.size());
        report.put("totalOmset", totalOmset);
        report.put("totalHpp", totalHpp);
        report.put("grossProfit", grossProfit);
        report.put("transactions", list);
        return report;
    }

    /**
     * Rekapitulasi penjualan bulanan.
     */
    public Map<String, Object> getMonthlySalesReport(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<Transaction> list = transactionDao.findByDateRange(start, end).stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .collect(Collectors.toList());

        double totalOmset = 0;
        double totalHpp = 0;

        for (Transaction t : list) {
            totalOmset += t.getGrandTotal();
            for (TransactionItem item : t.getItems()) {
                totalHpp += (item.getBuyPriceSnapshot() * item.getQuantity());
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("month", month);
        report.put("transactionCount", list.size());
        report.put("totalOmset", totalOmset);
        report.put("totalHpp", totalHpp);
        report.put("grossProfit", totalOmset - totalHpp);
        report.put("transactions", list);
        return report;
    }

    /**
     * Laporan produk terlaris berdasarkan kuantitas terjual.
     */
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        List<Transaction> all = transactionDao.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .collect(Collectors.toList());

        Map<Integer, Integer> qtyMap = new HashMap<>();
        Map<Integer, String> nameMap = new HashMap<>();
        Map<Integer, Double> revenueMap = new HashMap<>();

        for (Transaction t : all) {
            for (TransactionItem item : t.getItems()) {
                int pId = item.getProduct().getId();
                qtyMap.put(pId, qtyMap.getOrDefault(pId, 0) + item.getQuantity());
                nameMap.put(pId, item.getProduct().getName());
                revenueMap.put(pId, revenueMap.getOrDefault(pId, 0.0) + item.getLineTotal());
            }
        }

        return qtyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit > 0 ? limit : 10)
                .map(entry -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", entry.getKey());
                    row.put("productName", nameMap.get(entry.getKey()));
                    row.put("totalQuantity", entry.getValue());
                    row.put("totalRevenue", revenueMap.get(entry.getKey()));
                    return row;
                })
                .collect(Collectors.toList());
    }

    /**
     * Laporan valuasi aset persediaan barang saat ini.
     */
    public Map<String, Object> getInventoryValuationReport() {
        List<Product> products = productDao.findAll();
        int totalSku = products.size();
        int totalPhysicalStock = 0;
        double totalAssetValue = 0;

        for (Product p : products) {
            totalPhysicalStock += p.getStock();
            totalAssetValue += (p.getStock() * p.getBuyPrice());
        }

        Map<String, Object> report = new HashMap<>();
        report.put("totalSku", totalSku);
        report.put("totalUnits", totalPhysicalStock);
        report.put("totalValuation", totalAssetValue);
        report.put("products", products);
        return report;
    }
}

