
package com.smartretail.controller;

import com.smartretail.entity.Product;
import com.smartretail.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin
public class ReportController {

    private final ProductRepository productRepository;

    public ReportController(
            ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Map<String, Object> getReports() {

        List<Product> products = productRepository.findAll();

        long totalProducts = products.size();

        long totalStock = products.stream()
                .mapToLong(Product::getStockQuantity)
                .sum();

        long lowStockProducts = products.stream()
                .filter(p -> p.getStockQuantity() <= 10)
                .count();

        Map<String, Object> report = new HashMap<>();

        report.put("totalProducts", totalProducts);
        report.put("totalStock", totalStock);
        report.put("lowStockProducts", lowStockProducts);
        report.put("products", products);

        return report;
    }
}
