package com.telco.integration.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String manufacturer;

    @Column(precision = 19, scale = 2)
    private BigDecimal finalPriceHuf;

    private Integer stock;

    @Column(length = 64)
    private String ean;

    private Instant updatedAt;

    @Column(nullable = false, length = 20)
    private String source;

    @Column(nullable = false)
    private boolean valid;

    @Column(length = 1000)
    private String validationErrors;

    @Column(nullable = false, updatable = false)
    private Instant ingestedAt;

    @PrePersist
    public void prePersist() {
        if (ingestedAt == null) {
            ingestedAt = Instant.now();
        }
    }
}