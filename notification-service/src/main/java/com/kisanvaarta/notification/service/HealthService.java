package com.kisanvaarta.notification.service;

import com.kisanvaarta.notification.dto.HealthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthService {

    public HealthResponse getHealthStatus() {
        log.info("Calculating health status for notification-service");
        return new HealthResponse("UP", "notification-service");
    }
}
