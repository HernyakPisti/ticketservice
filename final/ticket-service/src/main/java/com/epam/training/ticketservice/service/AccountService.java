package com.epam.training.ticketservice.service;

import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private Optional<Account> loggedInAccount = Optional.empty();

    private final AccountRepository accountRepository;

    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void signIn(Account account) {
        this.setLoggedInAccount(Optional.of(account));
    }

    public void signOut() {
        this.setLoggedInAccount(Optional.empty());
    }

    public Optional<Account> getLoggedInAccount() {
        return loggedInAccount;
    }

    public Optional<Account> getAccountById(final String username) {
        return this.accountRepository.findById(username);
    }

    private void setLoggedInAccount(final Optional<Account> account) {
        this.loggedInAccount = account;
    }

}

