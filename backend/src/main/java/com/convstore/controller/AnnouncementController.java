package com.convstore.controller;

import com.convstore.model.Announcement;
import com.convstore.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:5173")
public class AnnouncementController {
    @Autowired private AnnouncementService service;
    @GetMapping public List<Announcement> getAll() { return service.getAll(); }
    @GetMapping("/active") public List<Announcement> getActive() { return service.getActive(); }
    @PostMapping public Announcement add(@RequestBody Announcement a) { return service.add(a); }
    @PutMapping("/{id}") public boolean update(@PathVariable int id, @RequestBody Announcement a) { a.setId(id); return service.update(a); }
    @DeleteMapping("/{id}") public boolean delete(@PathVariable int id) { return service.delete(id); }
}
