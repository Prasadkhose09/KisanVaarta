package com.kisanvaarta.price.service;

import com.kisanvaarta.price.dto.HealthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthService {

    public HealthResponse getHealthStatus() {
        log.info("Calculating health status for price-service");
        return new HealthResponse("UP", "price-service");
    }
}
