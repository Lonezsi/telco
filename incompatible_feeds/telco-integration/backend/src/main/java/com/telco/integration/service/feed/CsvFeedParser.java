package com.telco.integration.service.feed;

import com.opencsv.CSVReader;
import com.telco.integration.service.dto.RawFeedItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CsvFeedParser implements FeedParser {

    @Value("${feeds.csv.path}")
    private String csvPath;

    @Override
    public List<RawFeedItem> parse() {
        List<RawFeedItem> items = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] header = reader.readNext();
            if (header == null) {
                return items;
            }

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                headerMap.put(header[i].toLowerCase().trim(), i);
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                try {
                    items.add(mapRow(headerMap, line));
                } catch (Exception e) {
                    log.warn("Skipping invalid CSV row: {} | Error: {}", String.join(",", line), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to read CSV feed at path: {}", csvPath, e);
        }

        log.info("CSV parser loaded {} items", items.size());
        return items;
    }

    private RawFeedItem mapRow(Map<String, Integer> headerMap, String[] row) {
        return RawFeedItem.builder()
                .rawSku(getValue("sku", headerMap, row))
                .name(getValue("product_name", headerMap, row))
                .manufacturer(getValue("brand", headerMap, row))
                .price(parsePrice(getValue("gross_price_huf", headerMap, row)))
                .isGrossPrice(true)
                .stock(parseInteger(getValue("stock_qty", headerMap, row)))
                .sourceIdentifier("CSV")
                .build();
    }

    private String getValue(String column, Map<String, Integer> headerMap, String[] row) {
        Integer index = headerMap.get(column.toLowerCase());
        return (index != null && row.length > index) ? row[index] : null;
    }

    private BigDecimal parsePrice(String val) {
        if (val == null || val.isBlank())
            return null;
        // convert "12 700" -> "12700" and "12,70" -> "12.70"
        String cleanVal = val.replaceAll("\\s", "").replace(",", ".");
        return new BigDecimal(cleanVal);
    }

    private Integer parseInteger(String val) {
        if (val == null || val.isBlank())
            return null;
        return Integer.parseInt(val.trim());
    }

    @Override
    public String source() {
        return "CSV";
    }
}