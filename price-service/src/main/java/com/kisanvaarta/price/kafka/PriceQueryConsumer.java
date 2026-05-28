package com.kisanvaarta.price.kafka;

import com.kisanvaarta.price.entity.PriceRecord;
import com.kisanvaarta.price.repository.PriceRecordRepository;
import com.kisanvaarta.price.service.MandiApiClient;
import com.kisanvaarta.price.service.RedisCacheService;
import com.kisanvaarta.shared.events.FarmerQueryEvent;
import com.kisanvaarta.shared.events.PriceDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class PriceQueryConsumer {

    private final MandiApiClient mandiApiClient;
    private final RedisCacheService redisCacheService;
    private final PriceRecordRepository priceRecordRepository;
    private final KafkaTemplate<String, PriceDataEvent> kafkaTemplate;

    @Value("${kafka.topics.price-responses}")
    private String responseTopic;

    public PriceQueryConsumer(MandiApiClient mandiApiClient,
                              RedisCacheService redisCacheService,
                              PriceRecordRepository priceRecordRepository,
                              KafkaTemplate<String, PriceDataEvent> kafkaTemplate) {
        this.mandiApiClient = mandiApiClient;
        this.redisCacheService = redisCacheService;
        this.priceRecordRepository = priceRecordRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000)
    )
    @KafkaListener(topics = "farmer-queries", groupId = "price-service-group")
    public void consumeQuery(FarmerQueryEvent event) {
        log.info("Consumed FarmerQueryEvent: [{}]", event);

        PriceDataEvent priceData;
        String source;

        // 1. Check Redis cache first
        Optional<PriceDataEvent> cached = redisCacheService.getCached(event.getCropName(), event.getLocation());
        if (cached.isPresent()) {
            log.info("Cache HIT for [{}:{}]", event.getCropName(), event.getLocation());
            priceData = cached.get();
            source = "CACHE";
        } else {
            log.info("Cache MISS — calling Mandi API for [{}:{}]", event.getCropName(), event.getLocation());
            priceData = mandiApiClient.fetchPrice(event.getCropName(), event.getLocation());
            source = "API";

            // Cache it if it's a valid result (modalPrice is not -1.0)
            if (priceData.getModalPrice() != null && priceData.getModalPrice() >= 0) {
                redisCacheService.putCache(priceData);
            }
        }

        // 2. Complete the event with original query tracing information
        priceData.setQueryId(event.getQueryId());
        priceData.setFarmerPhone(event.getFarmerPhone());
        priceData.setLanguage(event.getLanguage());

        // 3. Save historical price record to MySQL database
        PriceRecord historicalRecord = PriceRecord.builder()
                .cropName(priceData.getCropName())
                .location(priceData.getLocation())
                .modalPrice(priceData.getModalPrice())
                .unit(priceData.getUnit())
                .priceDate(priceData.getPriceDate())
                .fetchedAt(LocalDateTime.now())
                .source(source)
                .build();
        priceRecordRepository.save(historicalRecord);
        log.info("Saved historical PriceRecord to database: [{}]", historicalRecord);

        // 4. Publish PriceDataEvent to price-responses Kafka topic
        kafkaTemplate.send(responseTopic, priceData.getQueryId(), priceData);
        log.info("Published PriceDataEvent successfully to topic [{}]: [{}]", responseTopic, priceData);
    }

    @DltHandler
    public void handleDlt(FarmerQueryEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Failed to process FarmerQueryEvent [{}] on topic [{}]. Event has been sent to DLT.", 
                event.getQueryId(), topic);
    }
}
