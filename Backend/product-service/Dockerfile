# Product Service Dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy parent pom first
COPY pom.xml .

# Copy gymstore-common completely (pom + src)
COPY gymstore-common/pom.xml ./gymstore-common/
COPY gymstore-common/src ./gymstore-common/src

# Copy all service pom.xml files (Maven needs to know about all modules)
COPY api-gateway/pom.xml ./api-gateway/
COPY product-service/pom.xml ./product-service/
COPY inventory-service/pom.xml ./inventory-service/
COPY cart-service/pom.xml ./cart-service/
COPY order-service/pom.xml ./order-service/
COPY payment-service/pom.xml ./payment-service/
COPY workflow-service/pom.xml ./workflow-service/
COPY notification-service/pom.xml ./notification-service/
COPY report-service/pom.xml ./report-service/

# Download dependencies (will be cached if pom.xml unchanged)
RUN mvn dependency:go-offline -pl product-service -am || true

# Copy product-service source
COPY product-service/src ./product-service/src

# Build the project
RUN mvn clean package -pl product-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl && \
    addgroup -S spring && adduser -S spring -G spring

COPY --from=build /build/product-service/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

