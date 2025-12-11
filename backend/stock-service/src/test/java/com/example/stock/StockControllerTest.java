package com.example.stock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StockControllerTest {

    private final StockController controller = new StockController();

    @Test
    void returnsConfiguredStockForKnownProduct() {
        int stock = assertDoesNotThrow(() -> controller.getStock("p1"));
        assertEquals(5, stock);
    }

    @Test
    void returnsZeroForUnknownProduct() {
        int stock = assertDoesNotThrow(() -> controller.getStock("unknown"));
        assertEquals(0, stock);
    }
}
