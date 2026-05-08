package com.convstore.service;

import com.convstore.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProductService {
    @Autowired
    private DataService dataService;

    public List<Product> getAllProducts() {
        return dataService.readData("products.json", new com.fasterxml.jackson.core.type.TypeReference<List<Product>>() {});
    }

    public Product getProductById(int id) {
        return getAllProducts().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void saveProducts(List<Product> products) {
        dataService.writeData("products.json", products);
    }

    public boolean addProduct(Product product) {
        List<Product> products = getAllProducts();
        product.setId(generateId(products));
        products.add(product);
        saveProducts(products);
        return true;
    }

    public boolean updateProduct(Product updatedProduct) {
        List<Product> products = getAllProducts();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updatedProduct.getId()) {
                products.set(i, updatedProduct);
                saveProducts(products);
                return true;
            }
        }
        return false;
    }

    public boolean deleteProduct(int id) {
        List<Product> products = getAllProducts();
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) {
            saveProducts(products);
        }
        return removed;
    }

    private int generateId(List<Product> products) {
        return products.stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0) + 1;
    }
}
