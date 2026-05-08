package com.convstore.service;

import com.convstore.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private DataService dataService;

    public List<Account> getAllAccounts() {
        return dataService.readData("accounts.json", new com.fasterxml.jackson.core.type.TypeReference<List<Account>>() {});
    }

    public Account authenticate(String username, String password) {
        List<Account> accounts = getAllAccounts();
        return accounts.stream()
                .filter(acc -> acc.getUsername().equals(username) && acc.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}
