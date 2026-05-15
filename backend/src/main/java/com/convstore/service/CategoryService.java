package com.convstore.service;

import com.convstore.model.ProductCategory;
import com.convstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired private CategoryRepository repo;
    public List<ProductCategory> getAll() { return repo.findAll(); }
    public ProductCategory add(ProductCategory c) { return repo.save(c); }
    public boolean update(ProductCategory c) {
        if (!repo.existsById(c.getId())) return false;
        repo.save(c); return true;
    }
    public boolean delete(int id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id); return true;
    }
}
