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
        repository.deleteAll();

        integrationService.ingestAndMerge();

        List<Product> products = repository.findAll();

        System.out.println("--- Integration Results ---");
        products.forEach(p -> System.out.printf("SKU: %s | Name: %s | Price: %.2f | Source: %s%n",
                p.getSku(), p.getName(), p.getFinalPriceHuf(), p.getSource()));

        assertFalse(products.isEmpty(), "Database should contain ingested products");

        assertTrue(products.size() >= 1, "Should have at least one product in the DB");

        products.stream()
                .filter(p -> p.getSku().contains("1001"))
                .findFirst()
                .ifPresent(p -> {
                    assertNotNull(p.getFinalPriceHuf());
                    System.out.println("Verified SKU1001 price: " + p.getFinalPriceHuf());
                });
    }
}