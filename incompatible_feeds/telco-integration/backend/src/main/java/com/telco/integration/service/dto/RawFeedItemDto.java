package com.telco.integration.service.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable DTO implementation of {@link RawFeedItem} with a Lombok-generated
 * builder.
 */
@Value
@Builder
public class RawFeedItemDto implements RawFeedItem {

    String rawSku;

    String name;

    String manufacturer;

    BigDecimal price;

    boolean grossPrice;

    Integer stock;

    String ean;

    Instant updatedAt;

    String sourceIdentifier;

    // Augment Lombok-generated builder to support the `isGrossPrice(...)` call-site
    // used elsewhere
    public static class RawFeedItemDtoBuilder {
        /**
         * bridges the gap between CSV 'gross_price' boolean and the builder's field
         */
        public RawFeedItemDtoBuilder isGrossPrice(boolean gross) {
            this.grossPrice = gross;
            return this;
        }
    }

}