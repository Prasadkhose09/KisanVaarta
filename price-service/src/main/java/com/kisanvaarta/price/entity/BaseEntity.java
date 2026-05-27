package com.kisanvaarta.price.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public class BaseEntity {
    // Shared columns (id, createdAt, etc.) can be defined here
}
