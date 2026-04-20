package com.brt.passenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * BRT Passenger Service
 *
 * Responsabilités :
 *  - Gestion des comptes passagers (CRUD)
 *  - Historique des trajets
 *  - Publication d'événements Kafka (PassengerRegistered, etc.)
 *  - Consommation de TicketValidated pour enrichir l'historique
 *  - Cache Redis pour les profils fréquemment consultés
 *
 * Port : 8082
 * Accessible uniquement via le gateway (8080)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PassagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassagerApplication.class, args);
    }
}
