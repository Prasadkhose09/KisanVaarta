#!/bin/bash
set -e

KAFKA_BROKER="kafka:9092"
PARTITIONS=3
REPLICATION_FACTOR=1

echo "Waiting for Kafka broker to be ready..."
until /opt/kafka/bin/kafka-broker-api-versions.sh --bootstrap-server $KAFKA_BROKER > /dev/null 2>&1; do
  echo "Kafka not ready yet — retrying in 3 seconds..."
  sleep 3
done

echo "Kafka is ready. Creating topics..."

TOPICS=("farmer-queries" "price-responses" "ai-responses" "notification-events")

for TOPIC in "${TOPICS[@]}"; do
  if /opt/kafka/bin/kafka-topics.sh --bootstrap-server $KAFKA_BROKER --list | grep -q "^$TOPIC$"; then
    echo "Topic '$TOPIC' already exists — skipping."
  else
    /opt/kafka/bin/kafka-topics.sh \
      --bootstrap-server $KAFKA_BROKER \
      --create \
      --topic $TOPIC \
      --partitions $PARTITIONS \
      --replication-factor $REPLICATION_FACTOR
    echo "Created topic: $TOPIC"
  fi
done

echo "All topics ready."
