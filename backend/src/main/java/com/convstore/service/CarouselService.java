package com.convstore.service;

import com.convstore.model.Carousel;
import com.convstore.repository.CarouselRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarouselService {
    @Autowired private CarouselRepository repo;
    public List<Carousel> getAll() { return repo.findAll(); }
    public List<Carousel> getActive() { return repo.findByActiveTrueOrderBySortOrderAsc(); }
    public Carousel add(Carousel c) { return repo.save(c); }
    public boolean update(Carousel c) {
        if (!repo.existsById(c.getId())) return false;
        repo.save(c); return true;
    }
    public boolean delete(int id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id); return true;
    }
}
