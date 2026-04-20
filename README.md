# 🚌 BRT Dakar — Microservices (Gateway + Passenger)

Architecture microservices Spring Boot 3.2 / Java 21 pour le système de transport BRT de Dakar.

---

## 📐 Architecture globale

```
Client (mobile / web)
        │
        ▼  :8080
   ┌─────────────────────────────────────────┐
   │              GATEWAY                    │
   │  • Routage  lb://service-name (Eureka)  │
   │  • Rate Limiting (Redis)                │
   │  • Circuit Breaker (Resilience4j)       │
   │  • Correlation ID injection             │
   │  • Logging / Tracing (Zipkin)           │
   └────────────────┬────────────────────────┘
                    │
        ┌───────────┼───────────────┐
        ▼           ▼               ▼
  :8081 auth   :8082 passenger  :8083 operation  ...
                    │
              ┌─────┼─────┐
              ▼     ▼     ▼
           Postgres Redis Kafka
```

---

## 🗂️ Structure du projet

```
brt-system/
├── docker-compose.yml              ← infrastructure complète
├── scripts/
│   └── create-multiple-dbs.sh     ← init PostgreSQL multi-db
│
├── gateway/                        ← port 8080
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/com/brt/gateway/
│       │   ├── GatewayApplication.java
│       │   ├── config/
│       │   │   └── GatewayConfig.java       ← KeyResolver rate limiter
│       │   ├── filter/
│       │   │   ├── CorrelationIdFilter.java ← X-Correlation-Id
│       │   │   └── LoggingFilter.java       ← logging latence
│       │   └── fallback/
│       │       └── FallbackController.java  ← réponses circuit breaker
│       └── resources/
│           └── application.yml
│
└── passenger-service/              ← port 8082
    ├── pom.xml
    ├── Dockerfile
    └── src/
        ├── main/java/com/brt/passenger/
        │   ├── PassengerApplication.java
        │   ├── domain/
        │   │   ├── model/
        │   │   │   ├── Passenger.java
        │   │   │   ├── PassengerStatus.java
        │   │   │   ├── Subscription.java
        │   │   │   ├── SubscriptionType.java
        │   │   │   ├── SubscriptionStatus.java
        │   │   │   └── TripHistory.java
        │   │   └── event/
        │   │       ├── PassengerEvents.java      ← événements produits
        │   │       └── TicketValidatedEvent.java ← événement consommé
        │   ├── repository/
        │   │   ├── PassengerRepository.java
        │   │   └── TripHistoryRepository.java
        │   ├── service/
        │   │   └── PassengerService.java         ← logique métier
        │   ├── controller/
        │   │   └── PassengerController.java      ← endpoints REST
        │   ├── dto/
        │   │   ├── request/
        │   │   │   ├── CreatePassengerRequest.java
        │   │   │   └── UpdatePassengerRequest.java
        │   │   └── response/
        │   │       ├── PassengerResponse.java
        │   │       ├── TripHistoryResponse.java
        │   │       └── ApiResponse.java
        │   ├── kafka/
        │   │   ├── PassengerEventProducer.java  ← publie sur Kafka
        │   │   └── PassengerEventConsumer.java  ← écoute ticket.validated
        │   ├── exception/
        │   │   ├── PassengerNotFoundException.java
        │   │   ├── DuplicatePassengerException.java
        │   │   └── GlobalExceptionHandler.java
        │   └── config/
        │       ├── RedisConfig.java
        │       └── KafkaConfig.java
        ├── main/resources/
        │   ├── application.yml
        │   └── db/migration/
        │       └── V1__create_passenger_schema.sql
        └── test/java/com/brt/passenger/
            ├── service/PassengerServiceTest.java
            └── controller/PassengerControllerTest.java
```

---

## 🚀 Démarrage rapide

### Prérequis
- Docker & Docker Compose
- Java 21
- Maven 3.9+

### 1. Lancer toute l'infrastructure

```bash
cd brt-system
docker-compose up -d postgres kafka redis zipkin
```

### 2. Attendre que tout soit prêt

```bash
docker-compose ps
# Vérifier que postgres, kafka et redis sont "healthy"
```

### 3. Lancer les services Spring Cloud (order important)

```bash
# Terminal 1 — Eureka
cd discovery-server && mvn spring-boot:run

# Terminal 2 — Config Server (si utilisé)
cd config-server && mvn spring-boot:run

# Terminal 3 — Gateway
cd gateway && mvn spring-boot:run

# Terminal 4 — Passenger Service
cd passenger-service && mvn spring-boot:run
```

### Ou tout en Docker

```bash
docker-compose up --build
```

---

## 🌐 API du Passenger Service

Tous les appels passent par le **gateway (port 8080)**.

### Créer un passager
```http
POST http://localhost:8080/api/v1/passengers
Content-Type: application/json

{
  "firstName": "Fatou",
  "lastName": "Diallo",
  "email": "fatou.diallo@brt.sn",
  "phoneNumber": "+221771234567"
}
```

**Réponse 201 :**
```json
{
  "success": true,
  "message": "Passager créé avec succès",
  "data": {
    "id": "a0000000-0000-0000-0000-000000000001",
    "firstName": "Fatou",
    "lastName": "Diallo",
    "fullName": "Fatou Diallo",
    "email": "fatou.diallo@brt.sn",
    "phoneNumber": "+221771234567",
    "status": "ACTIVE",
    "hasActiveSubscription": false,
    "createdAt": "2024-04-05T10:00:00Z",
    "updatedAt": "2024-04-05T10:00:00Z"
  },
  "timestamp": "2024-04-05T10:00:00Z"
}
```

### Récupérer un passager
```http
GET http://localhost:8080/api/v1/passengers/{id}
```

### Rechercher des passagers
```http
GET http://localhost:8080/api/v1/passengers/search?q=Fatou&page=0&size=10
```

### Mettre à jour (PATCH partiel)
```http
PATCH http://localhost:8080/api/v1/passengers/{id}
Content-Type: application/json

{
  "phoneNumber": "+221781234568"
}
```

### Historique des trajets
```http
GET http://localhost:8080/api/v1/passengers/{id}/trips?page=0&size=10
```

### Désactiver un compte
```http
DELETE http://localhost:8080/api/v1/passengers/{id}/deactivate?reason=Demande+du+passager
```

### Suspendre un compte
```http
PUT http://localhost:8080/api/v1/passengers/{id}/suspend?reason=Fraude+détectée
```

### Réactiver un compte
```http
PUT http://localhost:8080/api/v1/passengers/{id}/reactivate
```

---

## 📡 Topics Kafka

| Topic | Producteur | Consommateurs |
|-------|-----------|---------------|
| `passenger.registered` | passenger-service | auth-service, notification-service |
| `passenger.updated` | passenger-service | auth-service |
| `passenger.deactivated` | passenger-service | ticketing-service |
| `passenger.entered.station` | passenger-service | operation-service, analytics |
| `ticket.validated` | ticketing-service | **passenger-service** ← écoute |

---

## 🔌 URLs utiles

| Service | URL |
|---------|-----|
| Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Zipkin Traces | http://localhost:9411 |
| Passenger Actuator | http://localhost:8082/actuator/health |
| Passenger (via gateway) | http://localhost:8080/api/v1/passengers |

---

## 🧪 Lancer les tests

```bash
# Tests unitaires + intégration
cd passenger-service
mvn test

# Tests avec rapport
mvn test jacoco:report
```

---

## ⚙️ Variables d'environnement clés

| Variable | Défaut | Description |
|----------|--------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/passenger_db` | URL PostgreSQL |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka brokers |
| `SPRING_REDIS_HOST` | `localhost` | Redis host |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://localhost:8761/eureka/` | Eureka URL |
| `SPRING_ZIPKIN_BASE_URL` | `http://localhost:9411` | Zipkin URL |

---

## 🔒 Décisions d'architecture

**Pourquoi Spring Cloud Gateway et non Nginx ?**
Parce qu'il s'intègre nativement avec Eureka pour la découverte dynamique des services.
Quand un nouveau pod `passenger-service` démarre, le gateway le détecte automatiquement via `lb://passenger-service`.

**Pourquoi Redis pour le rate limiting ?**
Le gateway est potentiellement multi-instances. Redis centralise les compteurs de rate limiting pour que la limite soit cohérente quelle que soit l'instance de gateway qui reçoit la requête.

**Pourquoi Flyway et non `ddl-auto: create` ?**
Flyway versionne les migrations SQL. En production, `ddl-auto: create` détruirait les données à chaque redémarrage. Flyway applique uniquement les migrations non encore exécutées.

**Pourquoi `@Transactional(readOnly = true)` sur le service ?**
Optimisation JPA : les transactions en lecture seule désactivent le dirty checking d'Hibernate, réduisant la mémoire utilisée. Les méthodes d'écriture surchargent avec `@Transactional`.
