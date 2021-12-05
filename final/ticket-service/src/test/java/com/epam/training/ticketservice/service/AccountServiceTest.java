package com.epam.training.ticketservice.service;

import com.epam.training.ticketservice.model.Account;

import com.epam.training.ticketservice.repository.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class AccountServiceTest {


    private final static Account admin = new Account("admin", "admin", true);

    private AccountService underTest;

    @Mock
    private AccountRepository accountRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new AccountService(accountRepository);
    }

    @Test
    void testSignInShouldLogInGivenAccount() {
        // Given + When
        underTest.signIn(admin);

        // Then
        assertEquals(Optional.of(admin), underTest.getLoggedInAccount());
    }

    @Test
    void testSignOutShouldSetLoggedInAccountToOptimalEmpty() {
        // Given + When
        underTest.signOut();

        // Then
        assertEquals(Optional.empty(), underTest.getLoggedInAccount());
    }


    @Test
    void testGetAccountByIdShoulReturnEqualFindById() {
        // Given + When
        underTest.getAccountById(admin.getUsername());

        // Then
        verify(accountRepository).findById(admin.getUsername());
        verifyNoMoreInteractions(accountRepository);
    }


}