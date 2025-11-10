package com.example.catalog;

import com.example.catalog.model.Product;
import com.example.catalog.service.StockClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ProductController {

    private final StockClient stockClient;

    public ProductController(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @GetMapping("/products")
    public Mono<List<Product>> products() {
        return Mono.zip(
                stockClient.getStockMono("p1"),
                stockClient.getStockMono("p2"),
                stockClient.getStockMono("p3")
        ).map(tuple -> List.of(
                new Product("p1", "Laptop", 1499.0, tuple.getT1()),
                new Product("p2", "Smartphone", 699.0, tuple.getT2()),
                new Product("p3", "Headphones", 199.0, tuple.getT3())
        ));
    }
}