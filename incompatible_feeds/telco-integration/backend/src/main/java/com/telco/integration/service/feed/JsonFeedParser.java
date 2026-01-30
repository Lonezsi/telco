package com.telco.integration.service.feed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telco.integration.service.dto.RawFeedItem;
import com.telco.integration.service.dto.RawFeedItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonFeedParser implements FeedParser {

    @Value("${feeds.json.path}")
    private String jsonPath;

    private final ObjectMapper objectMapper;

    @Override
    public List<RawFeedItem> parse() {
        List<RawFeedItem> items = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(new File(jsonPath));
            if (root.isArray()) {
                for (JsonNode node : root) {
                    try {
                        items.add(mapNode(node));
                    } catch (Exception e) {
                        log.warn("Skipping invalid JSON node: {}",
                                node.toString().substring(0, Math.min(node.toString().length(), 50)));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to read JSON feed at {}", jsonPath, e);
        }
        log.info("JSON parser loaded {} items", items.size());
        return items;
    }

    private RawFeedItem mapNode(JsonNode node) {
        return RawFeedItemDto.builder()
                .rawSku(text(node, "id"))
                .name(text(node, "name"))
                .manufacturer(text(node, "manufacturer"))
                .price(decimal(node, "netPrice"))
                .isGrossPrice(false)
                .stock(integer(node, "quantityAvailable"))
                .ean(text(node, "ean"))
                .updatedAt(instant(node, "updatedAt"))
                .sourceIdentifier("JSON")
                .build();
    }

    // helperes
    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && !v.isNull()) ? v.asText() : null;
    }

    private BigDecimal decimal(JsonNode node, String field) {
        String val = text(node, field);
        return val != null ? new BigDecimal(val) : null;
    }

    private Integer integer(JsonNode node, String field) {
        String val = text(node, field);
        return val != null ? Integer.valueOf(val) : null;
    }

    private Instant instant(JsonNode node, String field) {
        String val = text(node, field);
        try {
            return val != null ? Instant.parse(val) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String source() {
        return "JSON";
    }
}