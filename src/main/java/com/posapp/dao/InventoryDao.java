package com.posapp.dao;

import com.posapp.model.StockMovement;
import java.time.LocalDate;
import java.util.List;

public interface InventoryDao {
    boolean insertMovement(StockMovement movement);
    List<StockMovement> findAllMovements();
    List<StockMovement> findMovementsByProductId(int productId);
    List<StockMovement> findMovementsByDateRange(LocalDate from, LocalDate to);
}

