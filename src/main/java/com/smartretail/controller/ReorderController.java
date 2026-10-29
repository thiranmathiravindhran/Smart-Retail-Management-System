
package com.smartretail.controller;

import com.smartretail.entity.Product;
import com.smartretail.entity.ReorderRequest;
import com.smartretail.repository.ProductRepository;
import com.smartretail.repository.ReorderRequestRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reorders")
@CrossOrigin
public class ReorderController {

    private final ReorderRequestRepository reorderRepository;
    private final ProductRepository productRepository;

    public ReorderController(
            ReorderRequestRepository reorderRepository,
            ProductRepository productRepository) {
        this.reorderRepository = reorderRepository;
        this.productRepository = productRepository;
    }

    // Get all reorder requests
    @GetMapping
    public ResponseEntity<List<ReorderRequest>> getAllReorders() {
        return ResponseEntity.ok(reorderRepository.findAll());
    }

    // Create a reorder request
    @PostMapping
    public ResponseEntity<?> createReorder(
            @RequestBody ReorderRequest request) {

        if (request.getProductId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Product ID is required."));
        }

        if (request.getRequestedQuantity() == null
                || request.getRequestedQuantity() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Quantity must be greater than zero."));
        }

        Product product = productRepository
                .findById(request.getProductId())
                .orElse(null);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found."));
        }

        request.setProductName(product.getProductName());
        request.setStatus("PENDING");
        request.setRequestedAt(LocalDateTime.now());

        ReorderRequest saved = reorderRepository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Receive stock and update the product stock quantity
    @PutMapping("/{id}/receive")
    @Transactional
    public ResponseEntity<?> receiveStock(@PathVariable Long id) {

        ReorderRequest request = reorderRepository
                .findById(id)
                .orElse(null);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message",
                            "Reorder request not found."));
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "This request has already been processed."));
        }

        if (request.getProductId() == null
                || request.getRequestedQuantity() == null
                || request.getRequestedQuantity() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Invalid product or requested quantity."));
        }

        Product product = productRepository
                .findById(request.getProductId())
                .orElse(null);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message",
                            "The product linked to this request was not found."));
        }

        int currentStock = product.getStockQuantity() == null
                ? 0 : product.getStockQuantity();

        int receivedQuantity = request.getRequestedQuantity();

        // Prevent integer overflow
        if (currentStock > Integer.MAX_VALUE - receivedQuantity) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Stock quantity is too large to update."));
        }

        int newStock = currentStock + receivedQuantity;

        product.setStockQuantity(newStock);
        productRepository.save(product);

        request.setStatus("RECEIVED");
        reorderRepository.save(request);

        return ResponseEntity.ok(Map.of(
                "message", "Stock received successfully.",
                "id", request.getId(),
                "productName", product.getProductName(),
                "receivedQuantity", receivedQuantity,
                "newStock", newStock,
                "status", "RECEIVED"
        ));
    }

    // Approve or reject a pending request
    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateReorderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        ReorderRequest request = reorderRepository
                .findById(id)
                .orElse(null);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message",
                            "Reorder request not found."));
        }

        String status = body.get("status");

        if (status == null
                || (!status.equalsIgnoreCase("APPROVED")
                && !status.equalsIgnoreCase("REJECTED"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Status must be APPROVED or REJECTED."));
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Only pending requests can be approved or rejected."));
        }

        request.setStatus(status.toUpperCase());
        ReorderRequest updated = reorderRepository.save(request);

        return ResponseEntity.ok(updated);
    }
}
