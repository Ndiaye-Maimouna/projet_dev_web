package com.brt.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * BRT API Gateway
 *
 * Point d'entrée unique pour tous les microservices BRT.
 * Responsabilités :
 *  - Routage intelligent via Eureka (lb://)
 *  - Rate Limiting par IP (Redis)
 *  - Circuit Breaker (Resilience4j)
 *  - Correlation ID injection
 *  - Distributed Tracing (Zipkin)
 *  - CORS global
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
