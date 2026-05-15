package com.convstore.service;

import com.convstore.model.Product;
import com.convstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void addStock(int productId, int quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    public boolean reduceStock(int productId, int quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getStock() < quantity) return false;
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        return true;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public boolean addProduct(Product product) {
        productRepository.save(product);
        return true;
    }

    public boolean updateProduct(Product updatedProduct) {
        if (!productRepository.existsById(updatedProduct.getId())) return false;
        productRepository.save(updatedProduct);
        return true;
    }

    public boolean deleteProduct(int id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }
}
