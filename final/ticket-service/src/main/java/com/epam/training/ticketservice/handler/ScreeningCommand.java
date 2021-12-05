package com.epam.training.ticketservice.handler;

import com.epam.training.ticketservice.model.Account;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.service.AccountService;
import com.epam.training.ticketservice.service.ScreeningService;
import com.epam.training.ticketservice.service.exception.NoScreeningException;
import com.epam.training.ticketservice.service.exception.OverlapException;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@ShellComponent
public class ScreeningCommand {

    private final ScreeningService screeningService;
    private final AccountService accountService;

    public ScreeningCommand(final ScreeningService screeningService, final AccountService accountService) {
        this.screeningService = screeningService;
        this.accountService = accountService;
    }

    @ShellMethod(value = "Add a screening to database", key = {"create screening", "cs"})
    public String createScreening(final String movieName, final String roomName, final String startingAt) {
        try {
            this.screeningService.createScreeningFromIds(movieName, roomName, startingAt);
            return String.format("Screening to '%s' in %s at %s successfully created.",
                    movieName, roomName, startingAt);
        } catch (NoScreeningException | OverlapException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "Delete a screening from the database", key = {"delete screening", "ds"})
    public String deleteScreening(final String movieName, final String roomName, final String startingAt) {
        try {
            this.screeningService.deleteScreening(this.screeningService.constructScreeningIdFromIds(
                    movieName,
                    roomName,
                    startingAt));
            return String.format("Screening to '%s' in %s at %s successfully deleted.",
                    movieName, roomName, startingAt);
        } catch (NoScreeningException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "List the screenings", key = {"list screenings", "ls"})
    public String listScreenings() {
        final List<Screening> screenings = this.screeningService.getAllScreenings();

        if (screenings.isEmpty()) {
            return "There are no screenings";
        } else {
            StringBuilder sb = new StringBuilder();
            Collections.reverse(screenings);
            screenings.forEach(screening -> sb.append(String.format(screening.getId().getMovie().getName() + " ("
                    + screening.getId().getMovie().getGenre() + ", "
                    + screening.getId().getMovie().getLength() + " minutes), screened in room "
                    + screening.getId().getRoom().getName() + ", at "
                    + screening.getId().getStartingAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + "\n")));
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }

    public Availability checkAdminAvailability() {
        return this.accountService.getLoggedInAccount().filter(Account::getAdmin).isPresent()
                ? Availability.available()
                : Availability.unavailable("this command requires admin privileges.");
    }
}