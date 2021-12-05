package com.epam.training.ticketservice.handler;


import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.model.ScreeningId;
import com.epam.training.ticketservice.service.AccountService;
import com.epam.training.ticketservice.service.ScreeningService;
import com.epam.training.ticketservice.service.exception.NoScreeningException;
import com.epam.training.ticketservice.service.exception.OverlapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.shell.Availability;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class ScreeningCommandTest {

    private final static Account ADMIN = new Account("admin", "admin", true);
    private final static Account USER = new Account("user", "user", false);
    private final static String movieName = "Movie";
    private final static String roomName = "Room";
    private final static String startingAt = "2000-12-13 10:10";

    private ScreeningCommand underTest;

    @Mock
    private AccountService accountService;

    @Mock
    private ScreeningService screeningService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new ScreeningCommand(screeningService, accountService);
    }

    @Test
    void testCreateScreeningShouldReturnErrorWhenNoScreeningExceptionOccurs() throws OverlapException, NoScreeningException {
        // Given
        final String expected = "Error";
        doThrow(new NoScreeningException(expected)).when(screeningService).createScreeningFromIds(movieName, roomName, startingAt);

        // When
        final String actual = underTest.createScreening(movieName, roomName, startingAt);

        // Then
        verify(screeningService).createScreeningFromIds(movieName, roomName, startingAt);
        assertEquals(expected, actual);
    }

    @Test
    void testCreateScreeningShouldReturnSuccesStringAfterScreeningCreated() throws OverlapException, NoScreeningException {
        // Given
        final String expected = "Screening to 'Movie' in Room at 2000-12-13 10:10 successfully created.";
        doNothing().when(screeningService).createScreeningFromIds(movieName, roomName, startingAt);

        // When
        final String actual = underTest.createScreening(movieName, roomName, startingAt);

        // Then
        verify(screeningService).createScreeningFromIds(movieName, roomName, startingAt);
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteScreeningShouldReturnErrorWhenExceptionOccurs() throws NoScreeningException {
        // Given
        final String expected = "Error";
        given(screeningService.constructScreeningIdFromIds(movieName, roomName, startingAt)).willReturn(new ScreeningId());
        doThrow(new NoScreeningException(expected)).when(screeningService).deleteScreening(any(ScreeningId.class));

        // When
        final String actual = underTest.deleteScreening(movieName, roomName, startingAt);

        // Then
        verify(screeningService).deleteScreening(any(ScreeningId.class));
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteScreeningShouldReturnSuccesStringAfterScreeningWasDeleted() throws NoScreeningException {
        // Given
        final String expected = "Screening to 'Movie' in Room at 2000-12-13 10:10 successfully deleted.";
        given(screeningService.constructScreeningIdFromIds(movieName, roomName, startingAt)).willReturn(new ScreeningId());
        doNothing().when(screeningService).deleteScreening(any(ScreeningId.class));

        // When
        final String actual = underTest.deleteScreening(movieName, roomName, startingAt);

        // Then
        verify(screeningService).deleteScreening(any(ScreeningId.class));
        assertEquals(expected, actual);
    }

    @Test
    void testListScreeningsShouldReturnStringWhenNoScreening() {
        // Given
        final String expected = "There are no screenings";
        given(screeningService.getAllScreenings()).willReturn(List.of());

        // When
        final String actual = underTest.listScreenings();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testCheckAdminShouldReturnUnavailableWhenNoAdminSignedIn() {
        // Given
        final Availability expected = Availability.unavailable("this command requires admin privileges.");
        given(accountService.getLoggedInAccount()).willReturn(Optional.empty());

        // When
        final Availability actual = underTest.checkAdminAvailability();

        // Then
        assertEquals(expected.isAvailable(), actual.isAvailable());
    }


    @Test
    void testCheckAdminShouldReturnUnavailableWhenUserTrySignIn() {
        // Given
        final Availability expected = Availability.unavailable("this command requires admin privileges.");
        given(accountService.getLoggedInAccount()).willReturn(Optional.of(USER));

        // When
        final Availability actual = underTest.checkAdminAvailability();

        // Then
        assertEquals(expected.isAvailable(), actual.isAvailable());
    }

    @Test
    void testCheckAdminShouldReturnAvailableWhenAdminIsSignedIn() {
        // Given
        final Availability expected = Availability.available();
        given(accountService.getLoggedInAccount()).willReturn(Optional.of(ADMIN));

        // When
        final Availability actual = underTest.checkAdminAvailability();

        // Then
        assertEquals(expected.isAvailable(), actual.isAvailable());
    }
}