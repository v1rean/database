package com.convstore.repository;

import com.convstore.model.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Integer> {
    List<InventoryLog> findAllByOrderByCreatedAtDesc();
}
