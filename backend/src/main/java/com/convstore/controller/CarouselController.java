package com.convstore.controller;

import com.convstore.model.Carousel;
import com.convstore.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carousels")
@CrossOrigin(origins = "http://localhost:5173")
public class CarouselController {
    @Autowired private CarouselService service;
    @GetMapping public List<Carousel> getAll() { return service.getAll(); }
    @GetMapping("/active") public List<Carousel> getActive() { return service.getActive(); }
    @PostMapping public Carousel add(@RequestBody Carousel c) { return service.add(c); }
    @PutMapping("/{id}") public boolean update(@PathVariable int id, @RequestBody Carousel c) { c.setId(id); return service.update(c); }
    @DeleteMapping("/{id}") public boolean delete(@PathVariable int id) { return service.delete(id); }
}
