package com.example.catalog.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jEventLogger {

    private static final Logger log = LoggerFactory.getLogger(Resilience4jEventLogger.class);

    private final RetryRegistry retryRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;

    public Resilience4jEventLogger(
            RetryRegistry retryRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry,
            TimeLimiterRegistry timeLimiterRegistry) {
        this.retryRegistry = retryRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.timeLimiterRegistry = timeLimiterRegistry;
    }

    @PostConstruct
    public void init() {
        // Register Retry events
        retryRegistry.getAllRetries().forEach(this::registerRetry);
        retryRegistry.getEventPublisher().onEntryAdded(event -> registerRetry(event.getAddedEntry()));

        // Register CircuitBreaker events
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(this::registerCircuitBreaker);
        circuitBreakerRegistry.getEventPublisher().onEntryAdded(event -> registerCircuitBreaker(event.getAddedEntry()));

        // Register TimeLimiter events
        timeLimiterRegistry.getAllTimeLimiters().forEach(this::registerTimeLimiter);
        timeLimiterRegistry.getEventPublisher().onEntryAdded(event -> registerTimeLimiter(event.getAddedEntry()));

        log.info("Resilience4j Event Loggers initialisés");
    }

    private void registerRetry(Retry retry) {
        retry.getEventPublisher()
                .onRetry(e -> {
                    log.warn("RETRY [{}] - Tentative #{}/{} après échec: {}",
                            e.getName(),
                            e.getNumberOfRetryAttempts(),
                            retry.getRetryConfig().getMaxAttempts(),
                            e.getLastThrowable().getClass().getSimpleName());
                })
                .onError(e -> {
                    log.error("RETRY [{}] - ÉCHEC FINAL après {} tentatives - Erreur: {}",
                            e.getName(),
                            e.getNumberOfRetryAttempts(),
                            e.getLastThrowable().getMessage());
                })
                .onSuccess(e -> {
                    if (e.getNumberOfRetryAttempts() > 0) {
                        log.info("RETRY [{}] - SUCCÈS après {} tentatives",
                                e.getName(),
                                e.getNumberOfRetryAttempts());
                    }
                });
    }

    private void registerCircuitBreaker(CircuitBreaker cb) {
        cb.getEventPublisher()
                .onStateTransition(e -> {
                    log.warn("CIRCUIT BREAKER [{}] - Transition: {} -> {}",
                            e.getCircuitBreakerName(),
                            e.getStateTransition().getFromState(),
                            e.getStateTransition().getToState());
                })
                .onError(e -> {
                    log.error("CIRCUIT BREAKER [{}] - Erreur enregistrée: {}",
                            e.getCircuitBreakerName(),
                            e.getThrowable().getClass().getSimpleName());
                })
                .onSuccess(e -> {
                    log.debug("CIRCUIT BREAKER [{}] - Appel réussi", e.getCircuitBreakerName());
                })
                .onCallNotPermitted(e -> {
                    log.warn("CIRCUIT BREAKER [{}] - Appel bloqué (circuit ouvert)",
                            e.getCircuitBreakerName());
                });
    }

    private void registerTimeLimiter(TimeLimiter tl) {
        tl.getEventPublisher()
                .onTimeout(e -> {
                    log.warn("TIME LIMITER [{}] - Timeout dépassé", e.getTimeLimiterName());
                })
                .onError(e -> {
                    log.error("TIME LIMITER [{}] - Erreur: {}",
                            e.getTimeLimiterName(),
                            e.getThrowable().getClass().getSimpleName());
                })
                .onSuccess(e -> {
                    log.debug("TIME LIMITER [{}] - Appel réussi", e.getTimeLimiterName());
                });
    }
}