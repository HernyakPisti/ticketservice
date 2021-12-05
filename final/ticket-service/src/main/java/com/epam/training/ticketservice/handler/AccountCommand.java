package com.epam.training.ticketservice.handler;

import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.service.AccountService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Optional;

@ShellComponent
public class AccountCommand {

    private final AccountService accountService;

    private Optional<Account> loggedInAccount = Optional.empty();

    public AccountCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    public Optional<Account> getLoggedInAccount() {
        return loggedInAccount;
    }

    @ShellMethod(value = "sign in privileged", key = "sign in privileged")
    public String signInPrivileged(final String username, final String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            this.loggedInAccount = Optional.of(new Account("admin", "admin", true));
            return "Signed in";
        } else {
            return "Login failed due to incorrect credentials";
        }
    }

    @ShellMethod(value = "sign out", key = "sign out")
    @ShellMethodAvailability(value = "checkLogged")
    public String signOut() {
        this.loggedInAccount = Optional.empty();
        return "Signed out";
    }

    @ShellMethod(value = "describe account", key = "describe account")

    public String describeAccount() {
        if (check()) {
            return String.format("Signed in with privileged account '%s'", loggedInAccount.orElseThrow().getUsername());
        } else {
            return "You are not signed in";
        }

    }

    public boolean check() {
        if (!this.loggedInAccount.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Availability checkLogged() {
        if (!this.loggedInAccount.isEmpty()) {
            return Availability.available();
        } else {
            return Availability.unavailable("you are not signed in");
        }
    }
}
