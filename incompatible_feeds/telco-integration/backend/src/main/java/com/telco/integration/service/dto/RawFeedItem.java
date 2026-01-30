package com.telco.integration.service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public interface RawFeedItem {

    String getRawSku();

    String getName();

    String getManufacturer();

    BigDecimal getPrice();

    boolean isGrossPrice();

    Integer getStock();

    String getEan();

    Instant getUpdatedAt();

    String getSourceIdentifier();
}
