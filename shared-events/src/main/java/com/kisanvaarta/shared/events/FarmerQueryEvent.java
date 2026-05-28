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
public class FarmerQueryEvent {
    private String queryId;        // UUID generated at gateway
    private String farmerPhone;    // e.g. "+919356544698"
    private String cropName;       // e.g. "tomato"
    private String location;       // e.g. "Nashik"
    private String language;       // "hindi", "marathi", "english"
    private LocalDateTime timestamp;
}
