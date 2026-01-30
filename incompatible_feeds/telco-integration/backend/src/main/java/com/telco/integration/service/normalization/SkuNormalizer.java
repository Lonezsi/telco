package com.telco.integration.service.normalization;

import org.springframework.stereotype.Component;

@Component
public class SkuNormalizer {
    /**
     * Normalizes SKU formats to a unified string.
     * Example: "P-1001" -> "SKU1001"
     * Example: "SKU-1001" -> "SKU1001"
     */
    public String normalize(String rawSku) {
        if (rawSku == null || rawSku.isBlank())
            return "UNKNOWN"; // TODO: reevaluate. this or null? hmm ._.

        String clean = rawSku.trim().replace("-", "").replace("_", "").toUpperCase();

        // 'P' -> 'SKU'
        if (clean.startsWith("P") && !clean.startsWith("PRO")) {
            return "SKU" + clean.substring(1);
        }

        return clean;
    }
}