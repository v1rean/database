package com.convstore.controller;

import com.convstore.model.ProductCategory;
import com.convstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {
    @Autowired private CategoryService service;
    @GetMapping public List<ProductCategory> getAll() { return service.getAll(); }
    @PostMapping public ProductCategory add(@RequestBody ProductCategory c) { return service.add(c); }
    @PutMapping("/{id}") public boolean update(@PathVariable int id, @RequestBody ProductCategory c) { c.setId(id); return service.update(c); }
    @DeleteMapping("/{id}") public boolean delete(@PathVariable int id) { return service.delete(id); }
}
