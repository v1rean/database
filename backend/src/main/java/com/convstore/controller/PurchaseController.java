package com.convstore.controller;

import com.convstore.model.PurchaseHistory;
import com.convstore.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:5173")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public List<PurchaseHistory> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/user/{userId}")
    public List<PurchaseHistory> getPurchasesByUser(@PathVariable int userId) {
        return purchaseService.getPurchasesByUserId(userId);
    }

    @PostMapping
    public boolean addPurchase(@RequestBody PurchaseHistory purchase) {
        return purchaseService.addPurchase(purchase);
    }

    @DeleteMapping("/{id}")
    public boolean deletePurchase(@PathVariable int id) {
        return purchaseService.deletePurchase(id);
    }
}
