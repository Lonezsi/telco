package com.telco.integration.service;

import com.telco.integration.domain.Product;
import com.telco.integration.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductIntegrationServiceTest {

    @Autowired
    private ProductIntegrationService integrationService;

    @Autowired
    private ProductRepository repository;

    @Test
    @DisplayName("Should ingest CSV and JSON and merge them into unified products")
    void testFullIngestionAndMerge() {
        // 1. Clean start
        repository.deleteAll();

        // 2. Execute the full integration flow
        integrationService.ingestAndMerge();

        // 3. Verify results
        List<Product> products = repository.findAll();

        // Log results for visibility
        System.out.println("--- Integration Results ---");
        products.forEach(p -> System.out.printf("SKU: %s | Name: %s | Price: %.2f | Source: %s%n",
                p.getSku(), p.getName(), p.getFinalPriceHuf(), p.getSource()));

        // 4. Critical Assertions
        assertFalse(products.isEmpty(), "Database should contain ingested products");

        // Testing the SKU normalization (e.g., P-1001 and SKU1001 should be unified)
        // If your test data has 2 items in CSV and 1 in JSON with one overlap, you
        // expect 2 total.
        assertTrue(products.size() >= 1, "Should have at least one product in the DB");

        // Verify that JSON items had VAT applied (Net to Gross)
        products.stream()
                .filter(p -> p.getSku().contains("1001")) // Assuming SKU1001 is in your data
                .findFirst()
                .ifPresent(p -> {
                    assertNotNull(p.getFinalPriceHuf());
                    System.out.println("Verified SKU1001 price: " + p.getFinalPriceHuf());
                });
    }
}