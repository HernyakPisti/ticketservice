package com.epam.training.ticketservice.handler;

import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.service.MovieService;
import com.epam.training.ticketservice.service.exception.NoMovieException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.shell.Availability;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class MovieCommandTest {

    private final static Account ADMIN = new Account("admin", "admin", true);
    private final static Account USER = new Account("user", "user", false);

    private MovieCommand underTest;

    @Mock
    private AccountCommand accountCommand;

    @Mock
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new MovieCommand(accountCommand, movieService);
    }

    @Test
    void testUpdateMovieShouldReturnErrorWhenExceptionThrows() throws NoMovieException {
        // Given
        final String expected = "Error";
        doThrow(new NoMovieException(expected)).when(movieService).updateMovie("Movie", "Genre", 100);

        // When
        final String actual = underTest.updateMovie("Movie", "Genre", 100);

        // Then
        verify(movieService).updateMovie("Movie", "Genre", 100);
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateMovieShouldCallServiceReturnSuccessMessage() throws NoMovieException {
        // Given
        final String expected = "Movie with name 'Movie' updated";
        doNothing().when(movieService).updateMovie("Movie", "Genre", 100);

        // When
        final String actual = underTest.updateMovie("Movie", "Genre", 100);

        // Then
        verify(movieService).updateMovie("Movie", "Genre", 100);
        assertEquals(expected, actual);
    }


    @Test
    void testDeleteMovieShouldCallServiceReturnSuccessMessage() throws NoMovieException {
        // Given
        final String expected = "Movie with name 'Movie' deleted";
        doNothing().when(movieService).deleteMovie("Movie");

        // When
        final String actual = underTest.deleteMovie("Movie");

        // Then
        verify(movieService).deleteMovie("Movie");
        assertEquals(expected, actual);
    }

    @Test
    void testListMoviesShouldReturnStringWhenNoMovieFound() {
        // Given
        final String expected = "There are no movies at the moment";
        given(movieService.getAllMovies()).willReturn(List.of());

        // When
        final String actual = underTest.listMovies();

        // Then
        assertEquals(expected, actual);
    }


    @Test
    void testCheckAdminShouldReturnUnavailableWhenNoAdminSignedIn() {
        // Given
        final Availability expected = Availability.unavailable("this command requires admin privileges.");
        given(accountCommand.getLoggedInAccount()).willReturn(Optional.empty());

        // When
        final Availability actual = underTest.checkAdmin();

        // Then
        assertEquals(expected.isAvailable(), actual.isAvailable());
    }


    @Test
    void testCheckAdminShouldReturnUnavailableWhenUserTrySignIn() {
        // Given
        final Availability expected = Availability.unavailable("this command requires admin privileges.");
        given(accountCommand.getLoggedInAccount()).willReturn(Optional.of(USER));

        // When
        final Availability actual = underTest.checkAdmin();

        // Then
        assertEquals(expected.isAvailable(), actual.isAvailable());
    }

    @Test
    void testCheckAdminShouldReturnAvailableWhenAdminIsSignedIn() {
        // Given
        final Availability expected = Availability.available();
        given(accountCommand.getLoggedInAccount()).willReturn(Optional.of(ADMIN));

        // When
        final Availability actual = underTest.checkAdmin();

        // Then
        assertEquals(expected.isAvailable(), actual.isAvailable());
    }
}