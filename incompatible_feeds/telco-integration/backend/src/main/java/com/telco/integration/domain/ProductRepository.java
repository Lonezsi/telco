package com.telco.integration.domain;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByValidTrue();

    List<Product> findByValidTrue(Sort sort);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findBySkuContainingIgnoreCase(String sku);

}