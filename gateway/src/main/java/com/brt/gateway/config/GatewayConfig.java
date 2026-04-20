package com.brt.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Configuration des beans du Gateway.
 *
 * Définit le resolver de clé pour le rate limiting.
 * Stratégie : limitation par adresse IP.
 * On peut facilement basculer vers userId si JWT est présent.
 */
@Configuration
public class GatewayConfig {

    /**
     * Rate limiting par IP.
     * Si tu veux limiter par utilisateur authentifié :
     *   exchange.getRequest().getHeaders().getFirst("X-User-Id")
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest()
                    .getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }
}
