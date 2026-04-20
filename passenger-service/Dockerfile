# ── Stage 1 : Build ────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven
COPY pom.xml .
# Télécharge les dépendances en cache layer séparé (optimisation Docker)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests

# ── Stage 2 : Runtime ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S brt && adduser -S brt -G brt
USER brt

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
