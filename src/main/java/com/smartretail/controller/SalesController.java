
package com.smartretail.controller;

import com.smartretail.entity.Product;
import com.smartretail.entity.Sales;
import com.smartretail.repository.ProductRepository;
import com.smartretail.repository.SalesRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesRepository salesRepository;
    private final ProductRepository productRepository;

    public SalesController(
            SalesRepository salesRepository,
            ProductRepository productRepository) {
        this.salesRepository = salesRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Sales> getAllSales() {
        return salesRepository.findAll();
    }

    @PostMapping
    public Sales addSale(@RequestBody SalesRequest request) {

        Product product = productRepository.findById(request.productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        if (request.quantitySold == null || request.quantitySold <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }

        if (request.saleDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Sale date is required");
        }

        if (request.totalAmount == null || request.totalAmount < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Total amount is invalid");
        }

        Sales sale = new Sales();
        sale.setProduct(product);
        sale.setQuantitySold(request.quantitySold);
        sale.setSaleDate(request.saleDate);
        sale.setTotalAmount(request.totalAmount);

        return salesRepository.save(sale);
    }

    public static class SalesRequest {
        public Long productId;
        public Integer quantitySold;
        public java.time.LocalDate saleDate;
        public Double totalAmount;
    }
}
