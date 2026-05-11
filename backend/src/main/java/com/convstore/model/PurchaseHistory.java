package com.convstore.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "purchases")
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    private String username;

    @Transient
    private List<PurchaseItem> items;

    private double total;
    private String date;

    public PurchaseHistory() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) { this.items = items; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
