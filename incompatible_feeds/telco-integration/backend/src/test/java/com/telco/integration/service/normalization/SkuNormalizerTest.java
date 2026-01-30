package com.telco.integration.service.normalization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkuNormalizerTest {
    private final SkuNormalizer normalizer = new SkuNormalizer();

    @ParameterizedTest
    @DisplayName("Should normalize various formats to a unified SKU")
    @CsvSource({
            "P-1001,    SKU1001", // CSV
            "p_1001,    SKU1001",
            "SKU-1001,  SKU1001", // JSON
            "PRO-500,   PRO500",
            " ' P-123 ', SKU123", // whitespace
            "abc-99,    ABC99"
    })
    void testNormalize(String input, String expected) {
        assertEquals(expected, normalizer.normalize(input));
    }

    @Test
    @DisplayName("Should handle empty or null inputs")
    void handleEdgeCases() {
        assertEquals("UNKNOWN", normalizer.normalize(null));
        assertEquals("UNKNOWN", normalizer.normalize("  "));
    }
}