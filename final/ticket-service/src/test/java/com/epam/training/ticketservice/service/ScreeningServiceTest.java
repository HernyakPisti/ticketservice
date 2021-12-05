package com.epam.training.ticketservice.service;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.model.ScreeningId;
import com.epam.training.ticketservice.repository.ScreeningRepository;
import com.epam.training.ticketservice.service.exception.NoScreeningException;
import com.epam.training.ticketservice.service.exception.OverlapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ScreeningServiceTest {

    private final static int breakLength = 10;
    private final static String dateTimePattern = "yyyy-MM-dd HH:mm";

    private final static Room room1 = new Room("Room1", 10, 10);
    private final static Movie movie1 = new Movie("Movie1", "action", 100);
    private final static Movie movie2 = new Movie("Movie2", "animation", 10);
    private final static String startingAt1 = "2000-12-13 10:10";
    private final static String startingAt2 = "2000-12-13 10:05";
    private final static String startingAt3 = "2000-12-13 09:55";
    private final static LocalDateTime formattedStartingAt1 = LocalDateTime.parse(startingAt1, DateTimeFormatter.ofPattern(dateTimePattern));
    private final static LocalDateTime formattedStartingAt2 = LocalDateTime.parse(startingAt2, DateTimeFormatter.ofPattern(dateTimePattern));
    private final static LocalDateTime formattedStartingAt3 = LocalDateTime.parse(startingAt3, DateTimeFormatter.ofPattern(dateTimePattern));
    private final static Screening screening = new Screening(new ScreeningId(movie1, room1, formattedStartingAt1));
    private final static Screening overlappingScreening = new Screening(new ScreeningId(movie2, room1, formattedStartingAt2));
    private final static Screening breakOverlappingScreening = new Screening(new ScreeningId(movie2, room1, formattedStartingAt3));

    private ScreeningService underTest;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new ScreeningService(screeningRepository, movieService, roomService, breakLength, dateTimePattern);
    }

    @Test
    void testGetAllScreeningsShouldReturnEqualFindAll() {
        // Given + When
        underTest.getAllScreenings();

        // Then
        verify(screeningRepository).findAll();
        verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    void testCreateScreeningShoulSaveToRepository() {
        // Given + When
        underTest.createScreening(screening);

        // Then
        verify(screeningRepository).save(screening);
        verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    void testCreateScreeningFromIdsShouldThrowNoScreeningExceptionWhenMovieDoesNotExist() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.empty());

        // When + Then
        assertThrows(NoScreeningException.class, () -> underTest.createScreeningFromIds(movie1.getName(), room1.getName(), startingAt1));
    }

    @Test
    void testCreateScreeningFromIdsShouldThrowNoScreeningExceptionWhenMovieExistsButRoomDoesNot() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(roomService.getRoomById(room1.getName())).willReturn(Optional.empty());

        // When + Then
        assertThrows(NoScreeningException.class, () -> underTest.createScreeningFromIds(movie1.getName(), room1.getName(), startingAt1));
    }

    @Test
    void testCreateScreeningFromIdsShouldThrowOverlapExceptionWhenValidScreeningIsOverlappingOtherScreening() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(roomService.getRoomById(room1.getName())).willReturn(Optional.of(room1));
        ScreeningService screeningService = spy(underTest);
        doReturn(true).when(screeningService).isOverlappingScreening(room1.getName(), movie1.getLength(), formattedStartingAt1);

        // When + Then
        assertThrows(OverlapException.class, () -> screeningService.createScreeningFromIds(movie1.getName(), room1.getName(), startingAt1));
    }

    @Test
    void testCreateScreeningFromIdsShouldThrowOverlapExceptionWhenValidScreeningIsOverlappingBreakAfterScreening() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(roomService.getRoomById(room1.getName())).willReturn(Optional.of(room1));
        ScreeningService screeningService = spy(underTest);
        doReturn(false).when(screeningService).isOverlappingScreening(room1.getName(), movie1.getLength(), formattedStartingAt1);
        doReturn(true).when(screeningService).isOverlappingBreak(room1.getName(), formattedStartingAt1);

        // When + Then
        assertThrows(OverlapException.class, () -> screeningService.createScreeningFromIds(movie1.getName(), room1.getName(), startingAt1));
    }


    @Test
    void testDeleteScreeningShouldTrowExceptionWhenIdIsNotFound() {
        // Given
        given(screeningRepository.findById(screening.getId())).willReturn(Optional.empty());

        // When + Then
        assertThrows(NoScreeningException.class, () -> underTest.deleteScreening(screening.getId()));
    }

    @Test
    void testGetScreeningByIdShouldReturnScreeningWhenStartingAtIsString() {
        // Given
        final ScreeningService screeningService = spy(underTest);

        // When
        screeningService.getScreeningById(movie1.getName(), room1.getName(), startingAt1);

        // Then
        verify(screeningService).getScreeningById(movie1.getName(), room1.getName(), formattedStartingAt1);
    }

    @Test
    void testConstructScreeningIdFromIdsShouldReturnNewScreeningIdWhenStartingAtIsLocalDateTime() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(roomService.getRoomById(room1.getName())).willReturn(Optional.of(room1));

        // When
        final ScreeningId actual = underTest.constructScreeningIdFromIds(movie1.getName(), room1.getName(), formattedStartingAt1);

        // Then
        assertEquals(screening.getId().toString(), actual.toString());
    }

    @Test
    void testConstructScreeningIdFromIdsShouldReturnNewScreeningIdWhenStartingAtIsString() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(roomService.getRoomById(room1.getName())).willReturn(Optional.of(room1));

        // When
        final ScreeningId actual = underTest.constructScreeningIdFromIds(movie1.getName(), room1.getName(), startingAt1);

        // Then
        assertEquals(screening.getId().toString(), actual.toString());
    }

    @Test
    void testIsOverlappingScreeningShouldReturnTrueWhenHasOverlappingScreening() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(movieService.getMovieById(overlappingScreening.getId().getMovie().getName()))
                .willReturn(Optional.of(overlappingScreening.getId().getMovie()));
        given(screeningRepository.findScreeningsById_Room_Name(room1.getName())).willReturn(List.of(overlappingScreening));

        // When
        final boolean actual = underTest.isOverlappingScreening(room1.getName(), movie1.getLength(), formattedStartingAt1);

        // Then
        assertTrue(actual);
    }

    @Test
    void testIsOverlappingScreeningShouldReturnFalseWhenDoesNotHaveOverlappingScreening() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(movieService.getMovieById(breakOverlappingScreening.getId().getMovie().getName()))
                .willReturn(Optional.of(breakOverlappingScreening.getId().getMovie()));
        given(screeningRepository.findScreeningsById_Room_Name(room1.getName())).willReturn(List.of(breakOverlappingScreening));

        // When
        final boolean actual = underTest.isOverlappingScreening(room1.getName(), movie1.getLength(), formattedStartingAt1);

        // Then
        assertFalse(actual);
    }

    @Test
    void testIsOverlappingScreeningShouldReturnTrueWhenHasBreakOverlappingScreening() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(movieService.getMovieById(breakOverlappingScreening.getId().getMovie().getName()))
                .willReturn(Optional.of(breakOverlappingScreening.getId().getMovie()));
        given(screeningRepository.findScreeningsById_Room_Name(room1.getName())).willReturn(List.of(breakOverlappingScreening));

        // When
        final boolean actual = underTest.isOverlappingBreak(room1.getName(), formattedStartingAt1);

        // Then
        assertTrue(actual);
    }

    @Test
    void testIsOverlappingScreeningShouldReturnFalseWhenDoesNotHaveBreakOverlappingScreening() {
        // Given
        given(movieService.getMovieById(movie1.getName())).willReturn(Optional.of(movie1));
        given(movieService.getMovieById(overlappingScreening.getId().getMovie().getName()))
                .willReturn(Optional.of(overlappingScreening.getId().getMovie()));
        given(screeningRepository.findScreeningsById_Room_Name(room1.getName())).willReturn(List.of(overlappingScreening));

        // When
        final boolean actual = underTest.isOverlappingBreak(room1.getName(), formattedStartingAt1);

        // Then
        assertFalse(actual);
    }
}