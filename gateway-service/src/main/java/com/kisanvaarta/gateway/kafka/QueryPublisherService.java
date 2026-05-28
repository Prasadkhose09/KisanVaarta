package com.kisanvaarta.gateway.kafka;

import com.kisanvaarta.shared.events.FarmerQueryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class QueryPublisherService {

    private final KafkaTemplate<String, FarmerQueryEvent> kafkaTemplate;

    @Value("${kafka.topics.farmer-queries}")
    private String topicName;

    public QueryPublisherService(KafkaTemplate<String, FarmerQueryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, FarmerQueryEvent>> publishQuery(FarmerQueryEvent event) {
        log.info("Published query [{}] for crop [{}] from phone [{}]", 
                event.getQueryId(), event.getCropName(), event.getFarmerPhone());

        CompletableFuture<SendResult<String, FarmerQueryEvent>> future = kafkaTemplate.send(topicName, event.getQueryId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Kafka publish success for query [{}]. Partition: [{}], Offset: [{}]", 
                        event.getQueryId(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Kafka publish failure for query [{}]. Error: ", event.getQueryId(), ex);
            }
        });

        return future;
    }
}
