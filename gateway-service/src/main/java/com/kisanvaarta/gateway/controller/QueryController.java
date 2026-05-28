package com.kisanvaarta.gateway.controller;

import com.kisanvaarta.gateway.dto.QueryRequest;
import com.kisanvaarta.gateway.dto.QueryResponse;
import com.kisanvaarta.gateway.service.IntentParserService;
import com.kisanvaarta.gateway.kafka.QueryPublisherService;
import com.kisanvaarta.shared.events.FarmerQueryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    private final IntentParserService intentParserService;
    private final QueryPublisherService queryPublisherService;

    @PostMapping("/query")
    public ResponseEntity<QueryResponse> handleQuery(@RequestBody QueryRequest request) {
        log.info("Received query from phone [{}]: {}", request.getPhone(), request.getMessage());
        FarmerQueryEvent event = intentParserService.parse(request.getMessage(), request.getPhone());
        queryPublisherService.publishQuery(event);
        return ResponseEntity.ok(QueryResponse.builder()
                .queryId(event.getQueryId())
                .status("RECEIVED")
                .message("Your query is being processed")
                .build());
    }
}
