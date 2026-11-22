package com.posapp.dao.csv;

import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.UserDao;
import com.posapp.model.*;
import com.posapp.model.enums.PaymentType;
import com.posapp.model.enums.TransactionStatus;
import com.posapp.util.CsvFileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionDaoCsv implements TransactionDao {
    private static final String TRANS_FILE = "transactions.csv";
    private static final String DETAILS_FILE = "transaction_details.csv";
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final List<String> TRANS_HEADER = Arrays.asList(
        "id", "invoice_number", "user_id", "transaction_date", "subtotal", "discount_amount", 
        "tax_amount", "grand_total", "payment_type", "amount_paid", "change_amount", 
        "reference_number", "status", "cancelled_by", "cancelled_at", "cancellation_reason"
    );

    private static final List<String> DETAILS_HEADER = Arrays.asList(
        "id", "transaction_id", "product_id", "quantity", "unit_price", 
        "buy_price_snapshot", "discount_amount", "line_total"
    );

    private final CsvFileManager fileManager;
    private final UserDao userDao;
    private final ProductDao productDao;

    public TransactionDaoCsv(CsvFileManager fileManager, UserDao userDao, ProductDao productDao) {
        this.fileManager = fileManager;
        this.userDao = userDao;
        this.productDao = productDao;
        try {
            this.fileManager.ensureFileWithHeader(TRANS_FILE, TRANS_HEADER);
            this.fileManager.ensureFileWithHeader(DETAILS_FILE, DETAILS_HEADER);
        } catch (IOException e) {
            throw new RuntimeException("Gagal inisialisasi file transaksi: " + e.getMessage(), e);
        }
    }

    public TransactionDaoCsv() {
        this(new CsvFileManager(), new UserDaoCsv(), new ProductDaoCsv());
    }

    @Override
    public synchronized boolean saveWithItems(Transaction transaction) {
        try {
            // 1. Generate new ID
            List<List<String>> transRows = fileManager.readAll(TRANS_FILE);
            int newTransId = 1;
            for (int i = 1; i < transRows.size(); i++) {
                int existingId = Integer.parseInt(transRows.get(i).get(0));
                if (existingId >= newTransId) {
                    newTransId = existingId + 1;
                }
            }
            transaction.setId(newTransId);

            // 2. Format transaction record
            Payment pay = transaction.getPayment();
            String payType = pay != null ? pay.getPaymentType().name() : PaymentType.CASH.name();
            double amtPaid = pay != null ? pay.getAmountPaid() : transaction.getGrandTotal();
            double change = (pay instanceof CashPayment) ? ((CashPayment) pay).getChange() : 0.0;
            String refNum = (pay instanceof QrisPayment) ? ((QrisPayment) pay).getTransactionReference() : "";

            List<String> transRecord = Arrays.asList(
                String.valueOf(transaction.getId()),
                transaction.getInvoiceNumber(),
                String.valueOf(transaction.getCashier() != null ? transaction.getCashier().getId() : 0),
                transaction.getTransactionDate().format(ISO_FMT),
                String.format(Locale.US, "%.2f", transaction.getSubtotal()),
                String.format(Locale.US, "%.2f", transaction.getDiscountAmount()),
                String.format(Locale.US, "%.2f", transaction.getTaxAmount()),
                String.format(Locale.US, "%.2f", transaction.getGrandTotal()),
                payType,
                String.format(Locale.US, "%.2f", amtPaid),
                String.format(Locale.US, "%.2f", change),
                refNum != null ? refNum : "",
                transaction.getStatus().name(),
                transaction.getCancelledBy() != null ? String.valueOf(transaction.getCancelledBy().getId()) : "",
                transaction.getCancelledAt() != null ? transaction.getCancelledAt().format(ISO_FMT) : "",
                transaction.getCancellationReason() != null ? transaction.getCancellationReason() : ""
            );

            // 3. Prepare details
            List<List<String>> detailRows = fileManager.readAll(DETAILS_FILE);
            int detailId = 1;
            for (int i = 1; i < detailRows.size(); i++) {
                int existingId = Integer.parseInt(detailRows.get(i).get(0));
                if (existingId >= detailId) {
                    detailId = existingId + 1;
                }
            }

            List<List<String>> newDetails = new ArrayList<>();
            for (TransactionItem item : transaction.getItems()) {
                item.setId(detailId);
                item.setTransactionId(newTransId);
                newDetails.add(Arrays.asList(
                    String.valueOf(detailId),
                    String.valueOf(newTransId),
                    String.valueOf(item.getProduct().getId()),
                    String.valueOf(item.getQuantity()),
                    String.format(Locale.US, "%.2f", item.getUnitPrice()),
                    String.format(Locale.US, "%.2f", item.getBuyPriceSnapshot()),
                    String.format(Locale.US, "%.2f", item.getDiscountAmount()),
                    String.format(Locale.US, "%.2f", item.getLineTotal())
                ));
                detailId++;
            }

            // 4. Append to files
            fileManager.append(TRANS_FILE, transRecord);
            for (List<String> det : newDetails) {
                fileManager.append(DETAILS_FILE, det);
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan transaksi ke CSV: " + e.getMessage(), e);
        }
    }

    @Override
    public Transaction findById(int id) {
        return findAll().stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Transaction findByInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null) return null;
        return findAll().stream()
                .filter(t -> invoiceNumber.equalsIgnoreCase(t.getInvoiceNumber()))
                .findFirst().orElse(null);
    }

    @Override
    public List<Transaction> findAll() {
        Map<Integer, Transaction> map = new LinkedHashMap<>();
        try {
            List<List<String>> transRows = fileManager.readAll(TRANS_FILE);
            for (int i = 1; i < transRows.size(); i++) {
                List<String> r = transRows.get(i);
                if (r.size() >= 13) {
                    int id = Integer.parseInt(r.get(0));
                    String invoice = r.get(1);
                    int userId = Integer.parseInt(r.get(2));
                    LocalDateTime date = LocalDateTime.parse(r.get(3), ISO_FMT);
                    double subtotal = Double.parseDouble(r.get(4));
                    double discount = Double.parseDouble(r.get(5));
                    double tax = Double.parseDouble(r.get(6));
                    double grandTotal = Double.parseDouble(r.get(7));
                    PaymentType payType = PaymentType.valueOf(r.get(8).toUpperCase());
                    double amtPaid = Double.parseDouble(r.get(9));
                    double change = Double.parseDouble(r.get(10));
                    String refNum = r.get(11);
                    TransactionStatus status = TransactionStatus.valueOf(r.get(12).toUpperCase());

                    User cashier = userDao.findById(userId);
                    Transaction t = new Transaction(invoice, cashier);
                    t.setId(id);
                    t.setTransactionDate(date);
                    t.setSubtotal(subtotal);
                    t.setDiscountAmount(discount);
                    t.setTaxAmount(tax);
                    t.setGrandTotal(grandTotal);
                    t.setStatus(status);

                    if (payType == PaymentType.CASH) {
                        CashPayment cp = new CashPayment(amtPaid);
                        cp.setChange(change);
                        t.setPayment(cp);
                    } else {
                        t.setPayment(new QrisPayment(amtPaid, refNum));
                    }

                    if (r.size() > 13 && !r.get(13).isEmpty()) {
                        t.setCancelledBy(userDao.findById(Integer.parseInt(r.get(13))));
                    }
                    if (r.size() > 14 && !r.get(14).isEmpty()) {
                        t.setCancelledAt(LocalDateTime.parse(r.get(14), ISO_FMT));
                    }
                    if (r.size() > 15 && !r.get(15).isEmpty()) {
                        t.setCancellationReason(r.get(15));
                    }

                    map.put(id, t);
                }
            }

            // Populate items from details file
            List<List<String>> detailRows = fileManager.readAll(DETAILS_FILE);
            for (int i = 1; i < detailRows.size(); i++) {
                List<String> r = detailRows.get(i);
                if (r.size() >= 8) {
                    int detId = Integer.parseInt(r.get(0));
                    int transId = Integer.parseInt(r.get(1));
                    int prodId = Integer.parseInt(r.get(2));
                    int qty = Integer.parseInt(r.get(3));
                    double unitPrice = Double.parseDouble(r.get(4));
                    double buySnapshot = Double.parseDouble(r.get(5));
                    double disc = Double.parseDouble(r.get(6));
                    double lineTotal = Double.parseDouble(r.get(7));

                    Transaction t = map.get(transId);
                    if (t != null) {
                        Product p = productDao.findById(prodId);
                        if (p == null) {
                            p = new Product(prodId, "UNKNOWN", "Produk #" + prodId, null, buySnapshot, unitPrice, 0, 0);
                        }
                        TransactionItem item = new TransactionItem(detId, transId, p, qty, unitPrice, buySnapshot, disc, lineTotal);
                        t.addRawItem(item);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca riwayat transaksi: " + e.getMessage(), e);
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public List<Transaction> findByDateRange(LocalDate from, LocalDate to) {
        return findAll().stream().filter(t -> {
            LocalDate tDate = t.getTransactionDate().toLocalDate();
            return (from == null || !tDate.isBefore(from)) && (to == null || !tDate.isAfter(to));
        }).collect(Collectors.toList());
    }

    @Override
    public synchronized boolean updateStatus(int transactionId, Transaction transaction) {
        List<Transaction> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == transactionId) {
                all.set(i, transaction);
                found = true;
                break;
            }
        }
        if (!found) return false;

        List<List<String>> records = new ArrayList<>();
        for (Transaction t : all) {
            Payment pay = t.getPayment();
            String payType = pay != null ? pay.getPaymentType().name() : PaymentType.CASH.name();
            double amtPaid = pay != null ? pay.getAmountPaid() : t.getGrandTotal();
            double change = (pay instanceof CashPayment) ? ((CashPayment) pay).getChange() : 0.0;
            String refNum = (pay instanceof QrisPayment) ? ((QrisPayment) pay).getTransactionReference() : "";

            records.add(Arrays.asList(
                String.valueOf(t.getId()),
                t.getInvoiceNumber(),
                String.valueOf(t.getCashier() != null ? t.getCashier().getId() : 0),
                t.getTransactionDate().format(ISO_FMT),
                String.format(Locale.US, "%.2f", t.getSubtotal()),
                String.format(Locale.US, "%.2f", t.getDiscountAmount()),
                String.format(Locale.US, "%.2f", t.getTaxAmount()),
                String.format(Locale.US, "%.2f", t.getGrandTotal()),
                payType,
                String.format(Locale.US, "%.2f", amtPaid),
                String.format(Locale.US, "%.2f", change),
                refNum != null ? refNum : "",
                t.getStatus().name(),
                t.getCancelledBy() != null ? String.valueOf(t.getCancelledBy().getId()) : "",
                t.getCancelledAt() != null ? t.getCancelledAt().format(ISO_FMT) : "",
                t.getCancellationReason() != null ? t.getCancellationReason() : ""
            ));
        }

        try {
            fileManager.writeAll(TRANS_FILE, TRANS_HEADER, records);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Gagal memperbarui status transaksi: " + e.getMessage(), e);
        }
    }
}

