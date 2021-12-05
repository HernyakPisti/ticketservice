package com.epam.training.ticketservice.handler;

import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

public class AccountCommandTest {

    private Account ADMIN = new Account("admin", "admin", true);
    private Account USER = new Account("user", "user", false);

    private AccountCommand underTest;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new AccountCommand(accountService);
    }

    @Test
    void testSignInPrivilegedShouldReturnLoginSuccessWhenUserIsAdmin() {
        // Given
        final String expected = "Signed in";
        given(accountService.getAccountById(ADMIN.getUsername())).willReturn(Optional.empty());

        // When
        final String actual = underTest.signInPrivileged(ADMIN.getUsername(), ADMIN.getPassword());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testSignOutShouldReturnSignOutSuccessWhenAdminSignOut() {
        // Given
        final String expected = "Signed out";
        given(accountService.getAccountById(ADMIN.getUsername())).willReturn(Optional.empty());

        // When
        final String actual = underTest.signOut();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testSignInPrivilegedShouldReturnLoginFailWhenTestUserSignInPrivileged() {
        // Given
        final String expected = "Login failed due to incorrect credentials";
        given(accountService.getAccountById(ADMIN.getUsername())).willReturn(Optional.empty());

        // When
        final String actual = underTest.signInPrivileged(USER.getUsername(), USER.getPassword());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testCheckLoggedInShouldReturnFalseWhenNobodyIsSignedIn() {
        // Given
        final Boolean expected = false;


        // When
        final Boolean actual = underTest.check();

        // Then
        assertEquals(expected, actual);
    }

}
