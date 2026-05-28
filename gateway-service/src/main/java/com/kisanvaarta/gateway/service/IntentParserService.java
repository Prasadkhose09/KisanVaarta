package com.kisanvaarta.gateway.service;

import com.kisanvaarta.shared.events.FarmerQueryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class IntentParserService {

    private static final List<String> CROPS = Arrays.asList("tomato", "onion", "potato", "wheat", "rice", "cotton", "soybean", "maize");
    private static final List<String> LOCATIONS = Arrays.asList("Nashik", "Pune", "Nagpur", "Aurangabad", "Kolhapur", "Solapur", "Amravati", "Satara");

    public FarmerQueryEvent parse(String rawMessage, String phone) {
        log.info("Parsing intent for raw message: [{}] from phone: [{}]", rawMessage, phone);

        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            rawMessage = "";
        }

        String queryId = UUID.randomUUID().toString();
        String cropName = extractCrop(rawMessage);
        String location = extractLocation(rawMessage);
        String language = detectLanguage(rawMessage);

        FarmerQueryEvent event = FarmerQueryEvent.builder()
                .queryId(queryId)
                .farmerPhone(phone)
                .cropName(cropName)
                .location(location)
                .language(language)
                .timestamp(LocalDateTime.now())
                .build();

        log.info("Successfully parsed event: [{}]", event);
        return event;
    }

    private String extractCrop(String message) {
        String lowerMessage = message.toLowerCase();
        for (String crop : CROPS) {
            if (lowerMessage.contains(crop)) {
                return crop;
            }
        }
        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            return "tomato";
        }
        String[] words = trimmed.split("\\s+");
        return words[0];
    }

    private String extractLocation(String message) {
        String lowerMessage = message.toLowerCase();
        for (String loc : LOCATIONS) {
            if (lowerMessage.contains(loc.toLowerCase())) {
                return loc;
            }
        }
        return "Pune";
    }

    private String detectLanguage(String message) {
        if (message.matches(".*[\\u0900-\\u097F].*")) {
            return "hindi";
        }
        return "english";
    }
}
