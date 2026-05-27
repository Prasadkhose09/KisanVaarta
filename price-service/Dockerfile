# Build stage
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy parent and modules configuration to cache dependencies
COPY pom.xml ./
COPY gateway-service/pom.xml ./gateway-service/
COPY price-service/pom.xml ./price-service/
COPY ai-engine-service/pom.xml ./ai-engine-service/
COPY notification-service/pom.xml ./notification-service/

# Cache dependencies inside Docker build cache mount
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B -DskipTests

# Copy source files
COPY gateway-service/src ./gateway-service/src
COPY price-service/src ./price-service/src
COPY ai-engine-service/src ./ai-engine-service/src
COPY notification-service/src ./notification-service/src

# Compile and package price-service
RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl price-service -am -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/price-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
