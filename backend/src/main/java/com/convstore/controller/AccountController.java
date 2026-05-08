package com.convstore.controller;

import com.convstore.model.Account;
import com.convstore.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Account login(@RequestBody Account loginRequest) {
        return accountService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
    }
}
