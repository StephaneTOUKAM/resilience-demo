# Resilience Demo (Spring Boot + Resilience4j + Prometheus/Grafana + Docker)

## Services
- **api-gateway** (8080): Spring Cloud Gateway, exposes `/api/**`
- **catalog-service** (8081): returns `/products` and calls stock-service with Resilience4j (Circuit Breaker/Retry/TimeLimiter)
- **stock-service** (8082): simule latence et erreurs

## Observabilité
- Actuator Prometheus: `/actuator/prometheus` sur chaque service
- Grafana (via docker-compose): http://localhost:3000  (user/pass: admin/admin — changez en prod)
- Prometheus: http://localhost:9090

## Lancer en local (Docker)
```
docker compose up --build
```
Puis:
- API produits: `http://localhost:8080/api/catalog/products`

## Consulter les metrics

### Commande pour voir le total des appels
```
resilience4j_circuitbreaker_calls_total
```
### Commande pour voir les états du circuit breaker
```
resilience4j_circuitbreaker_state
```

