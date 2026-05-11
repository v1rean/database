package com.convstore.controller;

import com.convstore.model.Account;
import com.convstore.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account loginRequest) {
        Account account = accountService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(account);
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String name = body.get("name");
        if (username == null || password == null || name == null) {
            return ResponseEntity.badRequest().build();
        }
        Account account = accountService.register(username, password, name);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}/preferences")
    public ResponseEntity<Map<String, Object>> getPreferences(@PathVariable int id) {
        Account account = accountService.getAccountById(id);
        if (account == null) return ResponseEntity.notFound().build();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("recommendEnabled", account.isRecommendEnabled());
        return ResponseEntity.ok(prefs);
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<Void> updatePreferences(@PathVariable int id, @RequestBody Map<String, Object> body) {
        Object val = body.get("recommendEnabled");
        if (val instanceof Boolean) {
            accountService.setRecommendEnabled(id, (Boolean) val);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody Map<String, String> body) {
        int userId = Integer.parseInt(body.get("userId"));
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (newPassword == null) return ResponseEntity.badRequest().build();
        boolean ok = accountService.changePassword(userId, oldPassword, newPassword);
        if (!ok) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String newPassword = body.get("newPassword");
        if (username == null || newPassword == null) {
            return ResponseEntity.badRequest().build();
        }
        boolean ok = accountService.forgotPassword(username, newPassword);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
        boolean ok = accountService.deleteAccount(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }
}
