package com.telco.integration.web;

import com.telco.integration.domain.Product;
import com.telco.integration.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository repository;

    @GetMapping
    public List<Product> getProducts(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "sku") String sort,
            @RequestParam(defaultValue = "false") boolean onlyValid) {
        Sort sortObj = Sort.by(sort).ascending();

        List<Product> products = onlyValid
                ? repository.findByValidTrue(sortObj)
                : repository.findAll(sortObj);

        if (filter == null || filter.isBlank()) {
            return products;
        }

        String f = filter.toLowerCase();

        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(f) ||
                        p.getSku().toLowerCase().contains(f))
                .toList();
    }
}