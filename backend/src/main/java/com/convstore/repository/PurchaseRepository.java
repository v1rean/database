package com.convstore.repository;

import com.convstore.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseHistory, Integer> {
    List<PurchaseHistory> findByUserId(int userId);
}
