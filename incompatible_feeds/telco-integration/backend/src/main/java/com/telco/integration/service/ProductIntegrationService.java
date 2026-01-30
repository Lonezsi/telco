package com.telco.integration.service;

import com.telco.integration.domain.Product;
import com.telco.integration.domain.ProductRepository;
import com.telco.integration.service.dto.RawFeedItem;
import com.telco.integration.service.feed.FeedParser;
import com.telco.integration.service.normalization.SkuNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductIntegrationService {

    private final ProductRepository repository;
    private final SkuNormalizer skuNormalizer;
    private final List<FeedParser> feedParsers;

    @Value("${tax.vat.multiplier:1.27}")
    private BigDecimal vatMultiplier;

    @Transactional
    public void ingestAndMerge() {
        Map<String, Product> mergedProducts = new HashMap<>();

        List<RawFeedItem> allItems = feedParsers.stream()
                .flatMap(parser -> parser.parse().stream())
                .collect(Collectors.toList());

        log.info("Loaded {} raw feed items", allItems.size());

        for (RawFeedItem item : allItems) {
            String normalizedSku = skuNormalizer.normalize(item.getRawSku());

            if ("UNKNOWN".equals(normalizedSku)) {
                String errorSku = "UNKNOWN-" + UUID.randomUUID().toString().substring(0, 8);
                repository.save(Objects.requireNonNull(mapToProduct(item, errorSku)));
                continue;
            }

            Product candidate = mapToProduct(item, normalizedSku);
            mergedProducts.merge(normalizedSku, candidate, (existing, newArrival) -> {
                Product winner = mergeProducts(existing, newArrival);
                winner.setSource("MERGED");
                return winner;
            });
        }

        repository.saveAll(Objects.requireNonNull(mergedProducts.values()));
        log.info("Merged into {} unified products", mergedProducts.size());
    }

    private Product mapToProduct(RawFeedItem item, String sku) {
        BigDecimal finalPrice = null;

        if (item.getPrice() != null) {
            finalPrice = item.isGrossPrice()
                    ? item.getPrice()
                    : item.getPrice().multiply(vatMultiplier);
        }

        List<String> errors = new ArrayList<>();
        if (sku.startsWith("UNKNOWN"))
            errors.add("Invalid SKU");
        if (item.getName() == null || item.getName().isBlank())
            errors.add("Missing Name");
        if (finalPrice == null)
            errors.add("Missing Price");

        // double promising no nulls rn
        return Objects.requireNonNull(
                Product.builder()
                        .sku(sku)
                        .name(item.getName() != null ? item.getName() : "UNKNOWN")
                        .manufacturer(item.getManufacturer())
                        .finalPriceHuf(finalPrice)
                        .stock(item.getStock())
                        .ean(item.getEan())
                        .updatedAt(item.getUpdatedAt())
                        .source(item.getSourceIdentifier())
                        .valid(errors.isEmpty())
                        .validationErrors(String.join(", ", errors))
                        .build());
    }

    private Product mergeProducts(Product existing, Product candidate) {

        Product winner;
        Product loser;

        if (candidate.getUpdatedAt() != null &&
                (existing.getUpdatedAt() == null || candidate.getUpdatedAt().isAfter(existing.getUpdatedAt()))) {
            winner = candidate;
            loser = existing;
        } else {
            winner = existing;
            loser = candidate;
        }

        if (winner.getManufacturer() == null)
            winner.setManufacturer(loser.getManufacturer());
        if (winner.getStock() == null)
            winner.setStock(loser.getStock());
        if (winner.getEan() == null)
            winner.setEan(loser.getEan());
        if (winner.getFinalPriceHuf() == null)
            winner.setFinalPriceHuf(loser.getFinalPriceHuf());

        if (!loser.isValid()) {
            winner.setValid(false);
            String existingErrors = winner.getValidationErrors() == null ? "" : winner.getValidationErrors();
            winner.setValidationErrors(
                    existingErrors + (existingErrors.isEmpty() ? "" : "; ") + loser.getValidationErrors());
        }

        return winner;
    }
}