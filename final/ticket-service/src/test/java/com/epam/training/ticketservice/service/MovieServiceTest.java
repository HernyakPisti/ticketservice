package com.epam.training.ticketservice.service;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class MovieServiceTest {

    private final static Movie movie1 = new Movie("Movie1", "action", 100);

    private MovieService underTest;

    @Mock
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new MovieService(movieRepository);
    }

    @Test
    void testGetMovieByIdShouldReturnEqualFindById() {
        // Given + When
        underTest.getMovieById(movie1.getName());

        // Then
        verify(movieRepository).findById(movie1.getName());
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void testGetMovieAllMoviesShoulReturnEqualFindAll() {
        // Given + When
        underTest.getAllMovies();

        // Then
        verify(movieRepository).findAll();
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void testCreateMovieShouldSaveMovieToRepository() {
        // Given + When
        underTest.createMovie(movie1);

        // Then
        verify(movieRepository).save(movie1);
        verifyNoMoreInteractions(movieRepository);
    }

}