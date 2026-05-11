package com.convstore.service;

import com.convstore.model.PurchaseHistory;
import com.convstore.model.PurchaseItem;
import com.convstore.repository.PurchaseItemRepository;
import com.convstore.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseItemRepository purchaseItemRepository;

    public List<PurchaseHistory> getAllPurchases() {
        List<PurchaseHistory> purchases = purchaseRepository.findAll();
        for (PurchaseHistory p : purchases) {
            p.setItems(purchaseItemRepository.findByPurchaseId(p.getId()));
        }
        return purchases;
    }

    public List<PurchaseHistory> getPurchasesByUserId(int userId) {
        List<PurchaseHistory> purchases = purchaseRepository.findByUserId(userId);
        for (PurchaseHistory p : purchases) {
            p.setItems(purchaseItemRepository.findByPurchaseId(p.getId()));
        }
        return purchases;
    }

    @Autowired
    private ProductService productService;

    @Transactional
    public Map<String, Object> checkout(PurchaseHistory purchase) {
        Map<String, Object> result = new HashMap<>();
        if (purchase.getItems() != null) {
            for (PurchaseItem item : purchase.getItems()) {
                boolean deducted = productService.reduceStock(item.getProductId(), item.getQuantity());
                if (!deducted) {
                    result.put("success", false);
                    result.put("message", "商品「" + item.getName() + "」库存不足");
                    return result;
                }
            }
        }
        PurchaseHistory saved = purchaseRepository.save(purchase);
        if (purchase.getItems() != null) {
            for (PurchaseItem item : purchase.getItems()) {
                item.setId(0);
                item.setPurchaseId(saved.getId());
                purchaseItemRepository.save(item);
            }
        }
        result.put("success", true);
        result.put("message", "购买成功");
        return result;
    }

    @Transactional
    public boolean addPurchase(PurchaseHistory purchase) {
        PurchaseHistory saved = purchaseRepository.save(purchase);
        if (purchase.getItems() != null) {
            for (PurchaseItem item : purchase.getItems()) {
                item.setId(0);
                item.setPurchaseId(saved.getId());
                purchaseItemRepository.save(item);
            }
            saved.setItems(purchaseItemRepository.findByPurchaseId(saved.getId()));
        }
        return true;
    }

    @Transactional
    public boolean deletePurchase(int id) {
        if (!purchaseRepository.existsById(id)) return false;
        purchaseItemRepository.deleteByPurchaseId(id);
        purchaseRepository.deleteById(id);
        return true;
    }
}
