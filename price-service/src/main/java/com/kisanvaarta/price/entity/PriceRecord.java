package com.kisanvaarta.price.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cropName;
    private String location;
    private Double modalPrice;
    private String unit;
    private LocalDate priceDate;
    private LocalDateTime fetchedAt;
    private String source;   // "API" or "CACHE"
}
