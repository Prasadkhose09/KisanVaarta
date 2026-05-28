package com.kisanvaarta.shared.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIAdvisoryEvent {
    private String queryId;
    private String farmerPhone;
    private String advisoryText;   // full message in farmer's language
    private String language;
    private LocalDateTime generatedAt;
}
