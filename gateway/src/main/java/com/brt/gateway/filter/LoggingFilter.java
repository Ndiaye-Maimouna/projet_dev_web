package com.brt.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Filtre global de logging.
 *
 * Logue chaque requête avec :
 *  - méthode HTTP + path
 *  - statut de la réponse
 *  - durée (latence en ms)
 *
 * Utile pour monitorer les performances et détecter les lenteurs.
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = Instant.now().toEpochMilli();

        String method = exchange.getRequest().getMethod().name();
        String path   = exchange.getRequest().getURI().getPath();

        log.info("→ Incoming: {} {}", method, path);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = Instant.now().toEpochMilli() - startTime;
            HttpStatus status = (HttpStatus) exchange.getResponse().getStatusCode();

            log.info("← Outgoing: {} {} | status={} | duration={}ms",
                    method, path,
                    status != null ? status.value() : "?",
                    duration);
        }));
    }

    @Override
    public int getOrder() {
        // Après CorrelationIdFilter (HIGHEST_PRECEDENCE + 1)
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
