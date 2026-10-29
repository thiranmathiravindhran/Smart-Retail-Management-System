package com.smartretail.controller;

import com.smartretail.entity.Product;
import com.smartretail.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/test")
    public Product testProduct() {
        Product product = new Product();
        product.setProductName("Rice 5kg");
        product.setCategory("Grocery");
        product.setPrice(650.0);
        product.setStockQuantity(25);
        return productRepository.save(product);
    }
}
