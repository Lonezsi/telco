package com.telco.integration.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String sku;
    private String name;
    private String manufacturer;
    private BigDecimal finalPriceHuf;
    private Integer stock;
    private String ean;
    private Instant updatedAt;
    private String source;
    private boolean isValid;
    @Column(length = 1000)
    private String validationErrors;
}