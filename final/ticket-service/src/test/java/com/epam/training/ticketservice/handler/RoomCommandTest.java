package com.epam.training.ticketservice.handler;

import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.service.RoomService;
import com.epam.training.ticketservice.service.exception.NoRoomException;
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
import static org.mockito.Mockito.verify;

class RoomCommandTest {

    private final static Account ADMIN = new Account("admin", "admin", true);
    private final static Account USER = new Account("user", "user", false);

    private RoomCommand underTest;

    @Mock
    private AccountCommand accountCommand;

    @Mock
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new RoomCommand(accountCommand, roomService);
    }

    @Test
    void testCreateRoomShouldReturnSuccessStringWhenRoomCreated() {
        // Given
        final String expected = "Room with name 'Room' created";
        doNothing().when(roomService).createRoom("Room", 10, 10);

        // When
        final String actual = underTest.createRoom("Room", 10, 10);

        // Then
        verify(roomService).createRoom("Room", 10, 10);
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateRoomShouldReturnSuccesStringWhenRoomUpdated() throws NoRoomException {
        // Given
        final String expected = "Room with name 'Room' updated";
        doNothing().when(roomService).updateRoom("Room", 10, 10);

        // When
        final String actual = underTest.updateRoom("Room", 10, 10);

        // Then
        verify(roomService).updateRoom("Room", 10, 10);
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteRoomShouldReturnSuccesStringWhenRoomDeleted() throws NoRoomException {
        // Given
        final String expected = "Room with name 'Room' deleted";
        doNothing().when(roomService).deleteRoom("Room");

        // When
        final String actual = underTest.deleteRoom("Room");

        // Then
        verify(roomService).deleteRoom("Room");
        assertEquals(expected, actual);
    }

    @Test
    void testListRoomsShouldReturnStringWhenNoRoomIs() {
        // Given
        final String expected = "There are no rooms at the moment";
        given(roomService.getAllRooms()).willReturn(List.of());

        // When
        final String actual = underTest.listRooms();

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