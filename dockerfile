# Multi-stage build
FROM maven:3.9-openjdk-17 AS builder

WORKDIR /build

# Копируем pom файлы
COPY pom.xml .
COPY auth-api/pom.xml auth-api/
COPY auth-domain/pom.xml auth-domain/
COPY auth-application/pom.xml auth-application/
COPY auth-infrastructure/pom.xml auth-infrastructure/
COPY auth-interfaces/pom.xml auth-interfaces/
COPY auth-bootstrap/pom.xml auth-bootstrap/

# Скачиваем зависимости
RUN mvn dependency:go-offline

# Копируем исходники
COPY . .

# Сборка
RUN mvn clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S auth && adduser -S auth -G auth
USER auth

WORKDIR /app

COPY --from=builder /build/auth-bootstrap/target/*.jar app.jar

EXPOSE 9090 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]