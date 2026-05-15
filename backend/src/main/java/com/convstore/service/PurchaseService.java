package com.convstore.service;

import com.convstore.model.InventoryLog;
import com.convstore.model.PurchaseHistory;
import com.convstore.model.PurchaseItem;
import com.convstore.repository.InventoryLogRepository;
import com.convstore.repository.PurchaseItemRepository;
import com.convstore.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private PurchaseItemRepository purchaseItemRepository;
    @Autowired private ProductService productService;
    @Autowired private InventoryLogRepository inventoryLogRepository;

    public List<PurchaseHistory> getAllPurchases() {
        List<PurchaseHistory> purchases = purchaseRepository.findAll();
        for (PurchaseHistory p : purchases) p.setItems(purchaseItemRepository.findByPurchaseId(p.getId()));
        return purchases;
    }

    public List<PurchaseHistory> getPurchasesByUserId(int userId) {
        List<PurchaseHistory> purchases = purchaseRepository.findByUserId(userId);
        for (PurchaseHistory p : purchases) p.setItems(purchaseItemRepository.findByPurchaseId(p.getId()));
        return purchases;
    }

    public List<PurchaseHistory> search(String start, String end, String username) {
        List<PurchaseHistory> purchases = purchaseRepository.search(start, end, username);
        for (PurchaseHistory p : purchases) p.setItems(purchaseItemRepository.findByPurchaseId(p.getId()));
        return purchases;
    }

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

    @Transactional
    public Map<String, Object> refund(int purchaseId, String operator) {
        Map<String, Object> result = new HashMap<>();
        Optional<PurchaseHistory> opt = purchaseRepository.findById(purchaseId);
        if (!opt.isPresent()) {
            result.put("success", false); result.put("message", "订单不存在"); return result;
        }
        PurchaseHistory purchase = opt.get();
        purchase.setPaymentMethod("[已退货]" + (purchase.getPaymentMethod() != null ? purchase.getPaymentMethod() : ""));
        purchaseRepository.save(purchase);

        List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchaseId);
        for (PurchaseItem item : items) {
            var prod = productService.getProductById(item.getProductId());
            int before = prod.getStock();
            productService.addStock(item.getProductId(), item.getQuantity());
            InventoryLog log = new InventoryLog();
            log.setProductId(item.getProductId()); log.setProductName(item.getName());
            log.setType("退货"); log.setQuantity(item.getQuantity());
            log.setStockBefore(before); log.setStockAfter(before + item.getQuantity());
            log.setOperator(operator); log.setNote("订单#" + purchaseId + "退货");
            log.setCreatedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            inventoryLogRepository.save(log);
        }
        result.put("success", true); result.put("message", "退货成功，已恢复库存");
        return result;
    }

    public Map<String, Object> salesReport(String start, String end) {
        Map<String, Object> report = new HashMap<>();
        List<PurchaseHistory> purchases = purchaseRepository.search(start, end, null);

        double totalSales = purchases.stream().mapToDouble(PurchaseHistory::getTotal).sum();
        int orderCount = purchases.size();

        Map<String, Integer> salesMap = new HashMap<>();
        for (PurchaseHistory p : purchases) {
            List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(p.getId());
            for (PurchaseItem item : items) {
                salesMap.merge(item.getName(), item.getQuantity(), Integer::sum);
            }
        }
        List<Map<String, Object>> ranking = salesMap.entrySet().stream()
                .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("name", e.getKey()); m.put("quantity", e.getValue()); return m; })
                .sorted((a, b) -> Integer.compare((int) b.get("quantity"), (int) a.get("quantity")))
                .collect(Collectors.toList());

        report.put("totalSales", totalSales);
        report.put("orderCount", orderCount);
        report.put("ranking", ranking);
        return report;
    }
}
