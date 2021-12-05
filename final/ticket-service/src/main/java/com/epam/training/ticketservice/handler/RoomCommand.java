package com.epam.training.ticketservice.handler;

import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.service.RoomService;
import com.epam.training.ticketservice.service.exception.NoRoomException;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;

@ShellComponent
public class RoomCommand {

    private final AccountCommand accountCommand;
    private final RoomService roomService;

    public RoomCommand(AccountCommand accountCommand, RoomService roomService) {
        this.accountCommand = accountCommand;
        this.roomService = roomService;
    }

    @ShellMethod(value = "create room", key = {"create room", "cr"})
    @ShellMethodAvailability(value = "checkAdmin")
    public String createRoom(final String name, final Integer rowCount, final Integer columnCount) {
        this.roomService.createRoom(name, rowCount, columnCount);
        return String.format("Room with name '%s' created", name);
    }

    @ShellMethod(value = "update room", key = "update room")
    @ShellMethodAvailability(value = "checkAdmin")
    public String updateRoom(final String name, final Integer rowCount, final Integer columnCount) {
        try {
            this.roomService.updateRoom(name, rowCount, columnCount);
            return String.format("Room with name '%s' updated", name);
        } catch (NoRoomException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "delete room", key = "delete room")
    @ShellMethodAvailability(value = "checkAdmin")
    public String deleteRoom(final String name) {
        try {
            this.roomService.deleteRoom(name);
            return String.format("Room with name '%s' deleted", name);
        } catch (NoRoomException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "list rooms", key = "list rooms")
    public String listRooms() {
        List<Room> rooms = this.roomService.getAllRooms();
        if (rooms.isEmpty()) {
            return "There are no rooms at the moment";
        } else {
            StringBuilder sb = new StringBuilder();
            rooms.forEach(r -> sb.append(String.format("Room "
                    + r.getName() + " with "
                    + r.getColumnNumber() * r.getRowNumber())
                    + " seats, " + r.getRowNumber()
                    + " rows and " + r.getColumnNumber()
                    + " columns\n"));
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }

    public Availability checkAdmin() {
        if (this.accountCommand.getLoggedInAccount().isPresent()
                && this.accountCommand.getLoggedInAccount().get().getAdmin()) {
            return Availability.available();
        } else {
            return Availability.unavailable("this command requires admin privileges");
        }
    }
}