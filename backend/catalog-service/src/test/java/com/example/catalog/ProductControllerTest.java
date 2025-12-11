package com.example.catalog;

import com.example.catalog.model.Product;
import com.example.catalog.service.StockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    private StockClient stockClient;

    @BeforeEach
    void setUp() {
        stockClient = mock(StockClient.class);
    }

    @Test
    void productsShouldCombineStockInformation() {
        when(stockClient.getStockMono("p1")).thenReturn(Mono.just(5));
        when(stockClient.getStockMono("p2")).thenReturn(Mono.just(0));
        when(stockClient.getStockMono("p3")).thenReturn(Mono.just(12));

        ProductController controller = new ProductController(stockClient);

        Mono<List<Product>> result = controller.products();

        StepVerifier.create(result)
                .assertNext(products -> {
                    assertEquals(3, products.size());

                    Product laptop = products.get(0);
                    assertEquals("p1", laptop.id());
                    assertEquals("Laptop", laptop.name());
                    assertEquals(1499.0, laptop.price());
                    assertEquals(5, laptop.stock());

                    Product smartphone = products.get(1);
                    assertEquals("p2", smartphone.id());
                    assertEquals("Smartphone", smartphone.name());
                    assertEquals(699.0, smartphone.price());
                    assertEquals(0, smartphone.stock());

                    Product headphones = products.get(2);
                    assertEquals("p3", headphones.id());
                    assertEquals("Headphones", headphones.name());
                    assertEquals(199.0, headphones.price());
                    assertEquals(12, headphones.stock());
                })
                .verifyComplete();

        verify(stockClient).getStockMono("p1");
        verify(stockClient).getStockMono("p2");
        verify(stockClient).getStockMono("p3");
    }
}
