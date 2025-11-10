package com.example.catalog.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class StockClient {

    private static final Logger log = LoggerFactory.getLogger(StockClient.class);

    private final WebClient webClient;

    public StockClient(@Value("${services.stock.base-url}") String stockBaseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(stockBaseUrl)
                .build();
    }

//    @CircuitBreaker(name = "stockService", fallbackMethod = "fallbackMono")
//    @Retry(name = "stockService", fallbackMethod = "fallbackMono")
    @TimeLimiter(name = "stockService", fallbackMethod = "fallbackMono")
    public Mono<Integer> getStockMono(String productId) {
        log.info("Tentative d'appel du stock-service pour le produit {}", productId);

        return webClient.get()
                .uri("/stock/{id}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> {
                    log.error("Stock service returned error status: {}", resp.statusCode());
                    return Mono.error(new RuntimeException("Stock service returned error: " + resp.statusCode()));
                })
                .bodyToMono(Integer.class)
                .timeout(Duration.ofSeconds(3))
                .doOnError(ex -> log.error("Erreur lors de l'appel : {}", ex.getMessage()));
    }

    public Mono<Integer> fallbackMono(String productId, Throwable t) {
        log.warn("Fallback activé pour le produit {} - Raison: {}",
                productId, t.getClass().getSimpleName() + ": " + t.getMessage());
        return Mono.just(0);
    }
}