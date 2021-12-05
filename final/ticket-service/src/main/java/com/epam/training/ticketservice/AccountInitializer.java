package com.epam.training.ticketservice;

import javax.annotation.PostConstruct;

import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.repository.AccountRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AccountInitializer {

    private static final Account ADMIN_ACCOUNT = new Account("admin", "admin", true);

    private final AccountRepository accountRepository;
    private final Environment environment;

    public AccountInitializer(AccountRepository accountRepository, Environment environment) {
        this.accountRepository = accountRepository;
        this.environment = environment;
    }

    @PostConstruct
    public void initProducts() {

        if (accountRepository.findById("admin").isEmpty()) {
            accountRepository.save(ADMIN_ACCOUNT);
        }
    }



}
