  package com.smartretail.repository;

import com.smartretail.entity.ReorderRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReorderRequestRepository
        extends JpaRepository<ReorderRequest, Long> {
}
