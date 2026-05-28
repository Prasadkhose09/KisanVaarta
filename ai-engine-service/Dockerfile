# Stage 1 — Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY shared-events/ ./shared-events/
COPY gateway-service/pom.xml ./gateway-service/
COPY price-service/pom.xml ./price-service/
COPY ai-engine-service/pom.xml ./ai-engine-service/
COPY notification-service/pom.xml ./notification-service/
COPY ai-engine-service/src ./ai-engine-service/src
RUN ./mvnw install -pl shared-events -am -DskipTests -q
RUN ./mvnw package -pl ai-engine-service -am -DskipTests -q

# Stage 2 — Extract layered JAR
FROM eclipse-temurin:21-jdk-alpine AS extractor
WORKDIR /app
COPY --from=builder /app/ai-engine-service/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3 — Final minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S kisan && adduser -S kisan -G kisan
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./
USER kisan
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
