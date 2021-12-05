package com.epam.training.ticketservice.service;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.model.Room;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.model.ScreeningId;
import com.epam.training.ticketservice.repository.ScreeningRepository;
import com.epam.training.ticketservice.service.exception.NoScreeningException;
import com.epam.training.ticketservice.service.exception.OverlapException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ScreeningService {

    private final int breakLength;
    private final String dateTimePattern;

    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    private final RoomService roomService;

    public ScreeningService( ScreeningRepository screeningRepository,
                             MovieService movieService,
                             RoomService roomService,
                             @Value("${ticket-service.screening.break-length}") int breakLength,
                             @Value("${ticket-service.date-time.pattern}") String dateTimePattern ) {
        this.screeningRepository = screeningRepository;
        this.movieService = movieService;
        this.roomService = roomService;
        this.breakLength = breakLength;
        this.dateTimePattern = dateTimePattern;
    }

    public Optional<Screening> getScreeningById( String movieName,
                                                 String roomName,
                                                 LocalDateTime startingAt ) {
        return this.screeningRepository.findById(constructScreeningIdFromIds(movieName, roomName, startingAt));
    }

    public Optional<Screening> getScreeningById(String movieName,
                                                String roomName,
                                                String startingAt) {
        final LocalDateTime parsedStartingAt = this.parseDateString(startingAt);

        return getScreeningById(movieName, roomName, parsedStartingAt);
    }

    public List<Screening> getAllScreenings() {
        return this.screeningRepository.findAll();
    }

    public void createScreeningFromIds(String movieName, String roomName, String startingAt)
            throws NoScreeningException, OverlapException {
        final LocalDateTime formattedStartingAt = this.parseDateString(startingAt);
        final Movie movie = this.movieService.getMovieById(movieName).orElseThrow(() ->
                new NoScreeningException("There is no movie with name: " + movieName));

        Room room = this.roomService.getRoomById(roomName).orElseThrow(() ->
                new NoScreeningException("There is no room with name: " + roomName));

        if (isOverlappingScreening(roomName, movie.getLength(), formattedStartingAt)) {
            throw new OverlapException("There is an overlapping screening");
        } else if (isOverlappingBreak(roomName, formattedStartingAt)) {
            throw new OverlapException(
                    "This would start in the break period after another screening in this room");
        }

        this.createScreening(new Screening(new ScreeningId(movie, room, formattedStartingAt)));
    }

    public void createScreening(Screening screening) {
        this.screeningRepository.save(screening);
    }

    public void deleteScreening(ScreeningId screeningId) throws NoScreeningException {
        this.screeningRepository.findById(screeningId)
                .map(screening -> {
                    this.screeningRepository.deleteById(screeningId);
                    return screening;
                })
                .orElseThrow(() -> new NoScreeningException(
                        String.format("There is no screening with %s movie name, %s room name, starting at %s",
                                screeningId.getMovie().getName(),
                                screeningId.getRoom().getName(),
                                screeningId.getStartingAt().format(DateTimeFormatter.ofPattern(dateTimePattern)))));
    }

    public ScreeningId constructScreeningIdFromIds(String movieName,
                                                   String roomName,
                                                   LocalDateTime startingAt) {
        final Movie movie = movieService.getMovieById(movieName).orElse(null);
        final Room room = roomService.getRoomById(roomName).orElse(null);

        return new ScreeningId(movie, room, startingAt);
    }

    public ScreeningId constructScreeningIdFromIds(String movieName,
                                                   String roomName,
                                                   String startingAt) {
        LocalDateTime formattedStartingAt = this.parseDateString(startingAt);

        return constructScreeningIdFromIds(movieName, roomName, formattedStartingAt);
    }

    public LocalDateTime parseDateString(String dateString) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(dateTimePattern));
    }

    public boolean isOverlappingScreening(String roomName,
                                          Integer movieLength,
                                          LocalDateTime startingAt) {
        final List<Screening> screenings = screeningRepository.findScreeningsById_Room_Name(roomName);
        final LocalDateTime endingAt = startingAt.plusMinutes(movieLength);

        return screenings.stream().anyMatch(screening -> {
            final int currentMovieLength = this.movieService.getMovieById(
                    screening.getId().getMovie().getName()).get().getLength();
            final LocalDateTime currentScreeningEndingAt =
                    screening.getId().getStartingAt().plusMinutes(currentMovieLength);

            return (startingAt.isAfter(screening.getId().getStartingAt())
                    && startingAt.isBefore(currentScreeningEndingAt))
                    ||
                    (endingAt.isAfter(screening.getId().getStartingAt())
                            && endingAt.isBefore(currentScreeningEndingAt));
        });
    }

    public boolean isOverlappingBreak(String roomName, LocalDateTime startingAt) {
        final List<Screening> screenings = screeningRepository.findScreeningsById_Room_Name(roomName);

        return screenings.stream().anyMatch(screening -> {
            final int currentMovieLength = this.movieService.getMovieById(
                    screening.getId().getMovie().getName()).get().getLength();
            final LocalDateTime currentScreeningEndingAt =
                    screening.getId().getStartingAt().plusMinutes(currentMovieLength);

            return startingAt.isAfter(currentScreeningEndingAt)
                    && startingAt.isBefore(currentScreeningEndingAt.plusMinutes(breakLength));
        });
    }
}