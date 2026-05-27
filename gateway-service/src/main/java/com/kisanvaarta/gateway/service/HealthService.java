package com.kisanvaarta.gateway.service;

import com.kisanvaarta.gateway.dto.HealthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthService {

    public HealthResponse getHealthStatus() {
        log.info("Calculating health status for gateway-service");
        return new HealthResponse("UP", "gateway-service");
    }
}
