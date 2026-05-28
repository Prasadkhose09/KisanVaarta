package com.kisanvaarta.price.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisanvaarta.shared.events.PriceDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
public class RedisCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<PriceDataEvent> getCached(String crop, String location) {
        String key = buildCacheKey(crop, location);
        log.info("Checking Redis cache for key: [{}]", key);
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                PriceDataEvent data = objectMapper.readValue(value, PriceDataEvent.class);
                log.info("Cache HIT for key: [{}], data: [{}]", key, data);
                return Optional.of(data);
            }
        } catch (Exception e) {
            log.error("Failed to read from Redis cache for key: [{}]", key, e);
        }
        log.info("Cache MISS for key: [{}]", key);
        return Optional.empty();
    }

    public void putCache(PriceDataEvent data) {
        if (data == null || data.getCropName() == null || data.getLocation() == null) {
            return;
        }
        String key = buildCacheKey(data.getCropName(), data.getLocation());
        log.info("Writing data to Redis cache for key: [{}]", key);
        try {
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(30));
            log.info("Successfully updated cache for key: [{}]", key);
        } catch (Exception e) {
            log.error("Failed to write to Redis cache for key: [{}]", key, e);
        }
    }

    private String buildCacheKey(String crop, String location) {
        // pattern: price:<cropName>:<location> (lowercase, no spaces)
        String cleanCrop = crop.toLowerCase().replaceAll("\\s+", "");
        String cleanLocation = location.toLowerCase().replaceAll("\\s+", "");
        return "price:" + cleanCrop + ":" + cleanLocation;
    }
}
