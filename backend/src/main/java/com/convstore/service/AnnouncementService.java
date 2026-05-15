package com.convstore.service;

import com.convstore.model.Announcement;
import com.convstore.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {
    @Autowired private AnnouncementRepository repo;
    public List<Announcement> getAll() { return repo.findAll(); }
    public List<Announcement> getActive() { return repo.findByActiveTrueOrderByCreatedAtDesc(); }
    public Announcement add(Announcement a) { return repo.save(a); }
    public boolean update(Announcement a) {
        if (!repo.existsById(a.getId())) return false;
        repo.save(a); return true;
    }
    public boolean delete(int id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id); return true;
    }
}
