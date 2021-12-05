package com.epam.training.ticketservice.service.exception;

public class RoomNameExistsException extends Throwable {
    public RoomNameExistsException(String message) {
        super(message);
    }
}
