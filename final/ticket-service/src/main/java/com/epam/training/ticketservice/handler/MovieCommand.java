package com.epam.training.ticketservice.handler;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.service.MovieService;
import com.epam.training.ticketservice.service.exception.NoMovieException;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;

@ShellComponent
public class MovieCommand {

    private final AccountCommand accountCommand;
    private final MovieService movieService;

    public MovieCommand(AccountCommand accountCommand, MovieService movieService) {
        this.accountCommand = accountCommand;
        this.movieService = movieService;
    }

    @ShellMethod(value = "create movie", key = "create movie")
    @ShellMethodAvailability(value = "checkAdmin")
    public String createMovie(final String name, final String genre, final int length) {
        this.movieService.createMovie(name, genre, length);
        return String.format("Movie with name '%s' created",name);
    }

    @ShellMethod(value = "update movie", key = "update movie")
    @ShellMethodAvailability(value = "checkAdmin")
    public String updateMovie(final String name, final String genre, final int length) {
        try {
            this.movieService.updateMovie(name, genre, length);
            return String.format("Movie with name '%s' updated", name);
        } catch (NoMovieException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "delete movie", key = "delete movie")
    @ShellMethodAvailability(value = "checkAdmin")
    public String deleteMovie(final String name) {
        try {
            this.movieService.deleteMovie(name);
            return String.format("Movie with name '%s' deleted", name);
        } catch (NoMovieException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "list movies", key = "list movies")
    public String listMovies() {
        List<Movie> movies = this.movieService.getAllMovies();
        if (movies.isEmpty()) {
            return "There are no movies at the moment";
        } else {
            StringBuilder sb = new StringBuilder();
            movies.forEach(m -> sb.append(String.format(m.getName()
                    + " (" + m.getGenre() + ", " + m.getLength()
                    + " minutes)\n")));
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