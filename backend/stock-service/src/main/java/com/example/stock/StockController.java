package com.example.stock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@RestController
public class StockController {

    private final Map<String,Integer> stock = Map.of(
            "p1", 5,
            "p2", 0,
            "p3", 12
    );

    @GetMapping("/stock/{id}")
    public int getStock(@PathVariable("id") String id) throws InterruptedException {
//        Thread.sleep(2500);
//        throw new RuntimeException("Erreur 500");
        return stock.getOrDefault(id, 0);
    }
}
