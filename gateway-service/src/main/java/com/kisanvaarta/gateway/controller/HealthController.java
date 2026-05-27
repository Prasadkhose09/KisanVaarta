package com.kisanvaarta.gateway.controller;

import com.kisanvaarta.gateway.dto.HealthResponse;
import com.kisanvaarta.gateway.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> getHealth() {
        log.info("Received request for health check in gateway-service");
        HealthResponse response = healthService.getHealthStatus();
        return ResponseEntity.ok(response);
    }
}
