#!/bin/sh

# Wait for the Kafka broker to be responsive
echo "Waiting for Kafka to be ready..."
until /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-broker:29092 --list > /dev/null 2>&1; do
  sleep 1
done

echo "Kafka is ready! Creating topics..."

/opt/kafka/bin/kafka-topics.sh --create --bootstrap-server kafka-broker:29092 --replication-factor 1 --partitions 3 --topic farmer-queries --if-not-exists
/opt/kafka/bin/kafka-topics.sh --create --bootstrap-server kafka-broker:29092 --replication-factor 1 --partitions 3 --topic price-responses --if-not-exists
/opt/kafka/bin/kafka-topics.sh --create --bootstrap-server kafka-broker:29092 --replication-factor 1 --partitions 3 --topic ai-responses --if-not-exists
/opt/kafka/bin/kafka-topics.sh --create --bootstrap-server kafka-broker:29092 --replication-factor 1 --partitions 3 --topic notification-events --if-not-exists

echo "Topics created successfully:"
/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-broker:29092 --list
