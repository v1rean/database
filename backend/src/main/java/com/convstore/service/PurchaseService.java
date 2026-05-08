package com.convstore.service;

import com.convstore.model.PurchaseHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    @Autowired
    private DataService dataService;

    public List<PurchaseHistory> getAllPurchases() {
        return dataService.readData("purchaseHistory.json", new com.fasterxml.jackson.core.type.TypeReference<List<PurchaseHistory>>() {});
    }

    public List<PurchaseHistory> getPurchasesByUserId(int userId) {
        return getAllPurchases().stream()
                .filter(p -> p.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public void savePurchases(List<PurchaseHistory> purchases) {
        dataService.writeData("purchaseHistory.json", purchases);
    }

    public boolean addPurchase(PurchaseHistory purchase) {
        List<PurchaseHistory> purchases = getAllPurchases();
        purchase.setId(generateId(purchases));
        purchases.add(purchase);
        savePurchases(purchases);
        return true;
    }

    public boolean deletePurchase(int id) {
        List<PurchaseHistory> purchases = getAllPurchases();
        boolean removed = purchases.removeIf(p -> p.getId() == id);
        if (removed) {
            savePurchases(purchases);
        }
        return removed;
    }

    private int generateId(List<PurchaseHistory> purchases) {
        return purchases.stream()
                .mapToInt(PurchaseHistory::getId)
                .max()
                .orElse(0) + 1;
    }
}
