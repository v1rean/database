package com.convstore.service;

import com.convstore.model.Account;
import com.convstore.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(int id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account authenticate(String username, String password) {
        Optional<Account> opt = accountRepository.findByUsername(username);
        if (opt.isPresent() && opt.get().getPassword().equals(password)) {
            return opt.get();
        }
        return null;
    }

    public Account register(String username, String password, String name) {
        if (accountRepository.findByUsername(username).isPresent()) return null;
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setName(name);
        account.setRole("user");
        return accountRepository.save(account);
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        Optional<Account> opt = accountRepository.findById(userId);
        if (opt.isPresent()) {
            Account acc = opt.get();
            if (!acc.getPassword().equals(oldPassword)) return false;
            acc.setPassword(newPassword);
            accountRepository.save(acc);
            return true;
        }
        return false;
    }

    public boolean setRecommendEnabled(int userId, boolean enabled) {
        Optional<Account> opt = accountRepository.findById(userId);
        if (opt.isPresent()) {
            Account acc = opt.get();
            acc.setRecommendEnabled(enabled);
            accountRepository.save(acc);
            return true;
        }
        return false;
    }

    public boolean deleteAccount(int id) {
        if (!accountRepository.existsById(id)) return false;
        accountRepository.deleteById(id);
        return true;
    }

    public boolean forgotPassword(String username, String newPassword) {
        Optional<Account> opt = accountRepository.findByUsername(username);
        if (opt.isPresent()) {
            Account acc = opt.get();
            acc.setPassword(newPassword);
            accountRepository.save(acc);
            return true;
        }
        return false;
    }
}
