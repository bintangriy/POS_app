package com.posapp.dao;

import com.posapp.model.Transaction;
import java.time.LocalDate;
import java.util.List;

public interface TransactionDao {
    boolean saveWithItems(Transaction transaction);
    Transaction findById(int id);
    Transaction findByInvoiceNumber(String invoiceNumber);
    List<Transaction> findAll();
    List<Transaction> findByDateRange(LocalDate from, LocalDate to);
    boolean updateStatus(int transactionId, Transaction transaction);
}

