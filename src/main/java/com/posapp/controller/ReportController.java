package com.posapp.controller;

import com.posapp.service.ReportService;
import com.posapp.util.CurrencyFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportController {

    // Daily Report Tab
    @FXML private DatePicker dpReportDate;
    @FXML private Label lblDailyCount;
    @FXML private Label lblDailyOmset;
    @FXML private Label lblDailyHpp;
    @FXML private Label lblDailyProfit;

    // Monthly Report Tab
    @FXML private ComboBox<Integer> cmbYear;
    @FXML private ComboBox<String> cmbMonth;
    @FXML private Label lblMonthlyCount;
    @FXML private Label lblMonthlyOmset;
    @FXML private Label lblMonthlyHpp;
    @FXML private Label lblMonthlyProfit;

    // Top Selling Tab
    @FXML private TableView<Map<String, Object>> tblTopSelling;
    @FXML private TableColumn<Map<String, Object>, String> colTopProduct;
    @FXML private TableColumn<Map<String, Object>, String> colTopQty;
    @FXML private TableColumn<Map<String, Object>, String> colTopRevenue;

    // Inventory Valuation Tab
    @FXML private Label lblValuationSku;
    @FXML private Label lblValuationUnits;
    @FXML private Label lblValuationTotal;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        dpReportDate.setValue(LocalDate.now());
        handleLoadDailyReport();

        setupMonthlyCombos();
        handleLoadMonthlyReport();

        setupTopSellingTable();
        handleLoadTopSelling();

        handleLoadInventoryValuation();
    }

    private void setupMonthlyCombos() {
        int currentYear = LocalDate.now().getYear();
        cmbYear.setItems(FXCollections.observableArrayList(currentYear - 1, currentYear, currentYear + 1));
        cmbYear.setValue(currentYear);

        cmbMonth.setItems(FXCollections.observableArrayList(
            "1 - Januari", "2 - Februari", "3 - Maret", "4 - April", "5 - Mei", "6 - Juni",
            "7 - Juli", "8 - Agustus", "9 - September", "10 - Oktober", "11 - November", "12 - Desember"
        ));
        cmbMonth.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
    }

    private void setupTopSellingTable() {
        colTopProduct.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().get("productName"))));
        colTopQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().get("totalQuantity"))));
        colTopRevenue.setCellValueFactory(c -> new SimpleStringProperty(
            CurrencyFormatter.formatRupiah((Double) c.getValue().get("totalRevenue"))
        ));
    }

    @FXML
    public void handleLoadDailyReport() {
        LocalDate date = dpReportDate.getValue() != null ? dpReportDate.getValue() : LocalDate.now();
        Map<String, Object> rep = reportService.getDailySalesReport(date);

        lblDailyCount.setText(rep.get("transactionCount") + " Transaksi");
        lblDailyOmset.setText(CurrencyFormatter.formatRupiah((Double) rep.get("totalOmset")));
        lblDailyHpp.setText(CurrencyFormatter.formatRupiah((Double) rep.get("totalHpp")));
        lblDailyProfit.setText(CurrencyFormatter.formatRupiah((Double) rep.get("grossProfit")));
    }

    @FXML
    public void handleLoadMonthlyReport() {
        int year = cmbYear.getValue() != null ? cmbYear.getValue() : LocalDate.now().getYear();
        int month = cmbMonth.getSelectionModel().getSelectedIndex() + 1;
        Map<String, Object> rep = reportService.getMonthlySalesReport(year, month);

        lblMonthlyCount.setText(rep.get("transactionCount") + " Transaksi");
        lblMonthlyOmset.setText(CurrencyFormatter.formatRupiah((Double) rep.get("totalOmset")));
        lblMonthlyHpp.setText(CurrencyFormatter.formatRupiah((Double) rep.get("totalHpp")));
        lblMonthlyProfit.setText(CurrencyFormatter.formatRupiah((Double) rep.get("grossProfit")));
    }

    @FXML
    public void handleLoadTopSelling() {
        List<Map<String, Object>> topList = reportService.getTopSellingProducts(10);
        tblTopSelling.setItems(FXCollections.observableArrayList(topList));
    }

    @FXML
    public void handleLoadInventoryValuation() {
        Map<String, Object> val = reportService.getInventoryValuationReport();
        lblValuationSku.setText(val.get("totalSku") + " SKU");
        lblValuationUnits.setText(val.get("totalUnits") + " Unit");
        lblValuationTotal.setText(CurrencyFormatter.formatRupiah((Double) val.get("totalValuation")));
    }
}

