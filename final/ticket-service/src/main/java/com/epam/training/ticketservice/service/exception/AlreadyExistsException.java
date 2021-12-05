package com.epam.training.ticketservice.service.exception;

public class AlreadyExistsException extends Throwable {
    public  AlreadyExistsException(String message)
    {
        super(message);
    }
}
