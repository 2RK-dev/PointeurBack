FROM gradle:8.8-jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle build

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

LABEL authors="Ryan"

ENTRYPOINT ["java", "-jar", "app.jar"]