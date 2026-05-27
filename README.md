# Kisan Vaarta

Kisan Vaarta is an AI-powered farmer market intelligence system designed to provide real-time agricultural data. 
The system features a decentralized, event-driven architecture powered by Spring Boot, Apache Kafka, Redis, and MySQL.

## How to Run

To build and spin up the complete environment including all microservices and infrastructure:

```bash
docker-compose up --build
```

This will:
1. Compile and build all Docker images for the 4 Spring Boot microservices.
2. Launch MySQL, Redis, Kafka, and Kafka UI.
3. Automatically execute `init-kafka-topics.sh` to initialize necessary Kafka topics.
4. Start the microservices once their infrastructure dependencies are healthy.

## How to Verify

You can verify that all 4 microservices are online and healthy by hitting their custom `/health` endpoint:

- **Gateway Service**: `http://localhost:8081/health`
- **Price Service**: `http://localhost:8082/health`
- **AI Engine Service**: `http://localhost:8083/health`
- **Notification Service**: `http://localhost:8084/health`

Expected response payload format:
```json
{
  "status": "UP",
  "service": "<service-name>"
}
```

To monitor Kafka topics and messages visually, open the Kafka UI:
- **Kafka UI**: `http://localhost:8080`
