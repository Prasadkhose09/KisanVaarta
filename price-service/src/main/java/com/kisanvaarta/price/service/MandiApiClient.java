package com.kisanvaarta.price.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kisanvaarta.shared.events.PriceDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class MandiApiClient {

    private final RestTemplate restTemplate;

    @Value("${mandi.api.base-url}")
    private String baseUrl;

    @Value("${mandi.api.api-key}")
    private String apiKey;

    public MandiApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PriceDataEvent fetchPrice(String cropName, String location) {
        long startTime = System.currentTimeMillis();
        log.info("Initiating Mandi API call for crop: [{}], location: [{}]", cropName, location);

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("api-key", apiKey)
                .queryParam("format", "json")
                .queryParam("filters[state]", "Maharashtra")
                .queryParam("filters[district]", location)
                .queryParam("filters[commodity]", cropName)
                .queryParam("limit", 1)
                .build()
                .toUriString();

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            long duration = System.currentTimeMillis() - startTime;
            log.info("Mandi API call completed. Crop: [{}], Location: [{}], Time elapsed: {} ms", 
                    cropName, location, duration);

            if (response != null && response.has("records") && response.get("records").isArray() && response.get("records").size() > 0) {
                JsonNode record = response.get("records").get(0);
                
                Double minPrice = getDoubleValue(record, "min_price");
                Double maxPrice = getDoubleValue(record, "max_price");
                Double modalPrice = getDoubleValue(record, "modal_price");
                String arrivalDateStr = getStringValue(record, "arrival_date");
                
                LocalDate priceDate = parseLocalDate(arrivalDateStr);

                PriceDataEvent event = PriceDataEvent.builder()
                        .cropName(cropName)
                        .location(location)
                        .minPrice(minPrice)
                        .maxPrice(maxPrice)
                        .modalPrice(modalPrice)
                        .unit("Quintal")
                        .priceDate(priceDate)
                        .build();

                log.info("Successfully fetched and mapped mandi price: [{}]", event);
                return event;
            } else {
                log.warn("Mandi API response has no records for crop: [{}], location: [{}]", cropName, location);
                return buildNotFoundEvent(cropName, location);
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Exception occurred during Mandi API call for crop: [{}], location: [{}]. Duration: {} ms. Exception: ", 
                    cropName, location, duration, e);
            return buildNotFoundEvent(cropName, location);
        }
    }

    private PriceDataEvent buildNotFoundEvent(String cropName, String location) {
        return PriceDataEvent.builder()
                .cropName(cropName)
                .location(location)
                .modalPrice(-1.0)
                .unit("Quintal")
                .priceDate(LocalDate.now())
                .build();
    }

    private Double getDoubleValue(JsonNode record, String fieldName) {
        if (record.has(fieldName) && !record.get(fieldName).isNull()) {
            try {
                return record.get(fieldName).asDouble();
            } catch (Exception e) {
                log.warn("Failed to parse field [{}] as double. Value: [{}]", fieldName, record.get(fieldName).asText());
            }
        }
        return null;
    }

    private String getStringValue(JsonNode record, String fieldName) {
        if (record.has(fieldName) && !record.get(fieldName).isNull()) {
            return record.get(fieldName).asText();
        }
        return null;
    }

    private LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            if (dateStr.contains("/")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if (dateStr.contains("-")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            log.warn("Failed to parse arrival date string: [{}], defaulting to today's date.", dateStr);
            return LocalDate.now();
        }
    }
}
