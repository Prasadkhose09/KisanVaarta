package com.kisanvaarta.shared.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceDataEvent {
    private String queryId;        // same UUID, traces the request end to end
    private String cropName;
    private String location;
    private Double minPrice;
    private Double maxPrice;
    private Double modalPrice;     // most common traded price — this is the real number
    private String unit;           // "Quintal"
    private LocalDate priceDate;
    private String farmerPhone;
    private String language;
}
