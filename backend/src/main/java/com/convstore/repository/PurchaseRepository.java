package com.convstore.repository;

import com.convstore.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseHistory, Integer> {
    List<PurchaseHistory> findByUserId(int userId);

    @Query("SELECT p FROM PurchaseHistory p WHERE " +
           "(:start IS NULL OR p.date >= :start) AND " +
           "(:end IS NULL OR p.date <= :end) AND " +
           "(:username IS NULL OR p.username LIKE %:username%) " +
           "ORDER BY p.id DESC")
    List<PurchaseHistory> search(@Param("start") String start,
                                 @Param("end") String end,
                                 @Param("username") String username);
}
