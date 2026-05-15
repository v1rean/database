package com.convstore.controller;

import com.convstore.model.PurchaseHistory;
import com.convstore.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:5173")
public class PurchaseController {
    @Autowired private PurchaseService purchaseService;

    @GetMapping
    public List<PurchaseHistory> getAllPurchases() { return purchaseService.getAllPurchases(); }

    @GetMapping("/user/{userId}")
    public List<PurchaseHistory> getPurchasesByUser(@PathVariable int userId) { return purchaseService.getPurchasesByUserId(userId); }

    @GetMapping("/search")
    public List<PurchaseHistory> search(@RequestParam(required = false) String start,
                                        @RequestParam(required = false) String end,
                                        @RequestParam(required = false) String username) {
        return purchaseService.search(start, end, username);
    }

    @PostMapping
    public boolean addPurchase(@RequestBody PurchaseHistory purchase) { return purchaseService.addPurchase(purchase); }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(@RequestBody PurchaseHistory purchase) { return purchaseService.checkout(purchase); }

    @PostMapping("/{id}/refund")
    public Map<String, Object> refund(@PathVariable int id, @RequestBody Map<String, String> body) {
        return purchaseService.refund(id, body.getOrDefault("operator", "管理员"));
    }

    @GetMapping("/sales-report")
    public Map<String, Object> salesReport(@RequestParam(required = false) String start,
                                           @RequestParam(required = false) String end) {
        return purchaseService.salesReport(start, end);
    }

    @DeleteMapping("/{id}")
    public boolean deletePurchase(@PathVariable int id) { return purchaseService.deletePurchase(id); }
}
