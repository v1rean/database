package com.convstore.service;

import com.convstore.model.InventoryLog;
import com.convstore.repository.InventoryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    @Autowired private InventoryLogRepository repo;
    public List<InventoryLog> getAll() { return repo.findAllByOrderByCreatedAtDesc(); }
    public InventoryLog add(InventoryLog log) { return repo.save(log); }
}
