package com.epam.training.ticketservice.service;

import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.repository.RoomRepository;
import com.epam.training.ticketservice.service.exception.NoRoomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class RoomServiceTest {

    private final static Room room1 = new Room("Room1", 10, 20);

    private RoomService underTest;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new RoomService(roomRepository);
    }

    @Test
    void testGetRoomByIdShouldReturnEqualFindById() {
        // Given + When
        underTest.getRoomById(room1.getName());

        // Then
        verify(roomRepository).findById(room1.getName());
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    void testGetRoomAllRoomsShouldReturnEqualFindALl() {
        // Given + When
        underTest.getAllRooms();

        // Then
        verify(roomRepository).findAll();
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    void testCreateRoomShouldSaveToRepository() {
        // Given + When
        underTest.createRoom(room1);

        // Then
        verify(roomRepository).save(room1);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    void testUpdateRoomShouldTrowExceptionWhenIdIsNotFound() {
        // Given
        given(roomRepository.findById(room1.getName())).willReturn(Optional.empty());

        // When + Then
        assertThrows(NoRoomException.class, () -> underTest.updateRoom("Room", 10, 10));
    }

    @Test
    void testDeleteRoomShouldTrowExceptionWhenIdIsNotFound() {
        // Given
        final String roomName = room1.getName();
        given(roomRepository.findById(roomName)).willReturn(Optional.empty());

        // When + Then
        assertThrows(NoRoomException.class, () -> underTest.deleteRoom(roomName));
    }


}
