package com.kisanvaarta.aiengine.service;

import com.kisanvaarta.aiengine.dto.HealthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthService {

    public HealthResponse getHealthStatus() {
        log.info("Calculating health status for ai-engine-service");
        return new HealthResponse("UP", "ai-engine-service");
    }
}
