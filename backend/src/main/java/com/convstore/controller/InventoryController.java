package com.convstore.controller;

import com.convstore.model.InventoryLog;
import com.convstore.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:5173")
public class InventoryController {
    @Autowired private InventoryService service;
    @GetMapping public List<InventoryLog> getAll() { return service.getAll(); }
    @PostMapping public InventoryLog add(@RequestBody InventoryLog log) { return service.add(log); }
}
