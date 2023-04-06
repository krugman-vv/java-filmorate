package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;
import static java.time.Month.DECEMBER;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static long counter = 0;
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate MIN_DAY_RELEASE = LocalDate.of(1895, DECEMBER, 28);
    private final Map<Long, Film> filmsByID = new HashMap<>();

    @Override
    public Film create(Film film) {
        log.info("POST request received: {}", film);

        validateFilm(film);

        film.setId(setFilmId());
        film.setUsersLikes(new HashSet<>());
        filmsByID.put(film.getId(), film);

        log.info("A new film has been added: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("PUT request received: {}", film);

        validateFilm(film);

        long filmId = film.getId();
        checkFilmForExist(
                filmId,
                "Updating a movie is not possible. The movie is not found, ID="
        );

        film.setUsersLikes(filmsByID.get(filmId).getUsersLikes());
        filmsByID.put(filmId, film);
        log.info("The movie has been updated: {}", film);

        return film;
    }

    @Override
    public Film delete(Film film) {
        checkFilmForExist(
                film.getId(),
                "Deleting a movie is not possible. The movie was not found, ID="
        );

        filmsByID.remove(film.getId());
        log.info("The movie has been deleted: {}", film);

        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmsByID.values();
    }

    @Override
    public Film getFilmById(long id) {
        checkFilmForExist(
                id,
                "Getting an existing movie is not possible. The movie was not found, ID="
        );
        return filmsByID.get(id);
    }

    @Override
    public Film addUserLike(long filmId, long userId) {
        checkFilmForExist(
                filmId,
                "Adding a user's like is not possible. The movie was not found, ID="
        );

        makeUserLike(filmId, userId);

        return filmsByID.get(filmId);
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        checkFilmForExist(
                filmId,
                "Deleting the user's like is not possible. A movie was not found, ID="
        );

        unlike(filmId, userId);

        return filmsByID.get(filmId);
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        log.info("Getting the list of popular films.");
        Collection<Film> topFilms = getAllFilms();
        return topFilms.stream()
                .sorted((o1, o2) -> {
                    int comp = compare(o1.getUsersLikes().size(), o2.getUsersLikes().size());
                    return -1 * comp;
                }).limit(count)
                .collect(Collectors.toList());
    }

    public void unlike(long filmId, long userId) {
        if (!filmsByID.get(filmId).getUsersLikes().contains(userId)) {
            printErrorMessage(String.format(
                    "The user ID=%s didn't like the film with ID=%s", userId, filmId
            ));
        }

        filmsByID.get(filmId).getUsersLikes().remove(userId);
        log.info("The like of user with ID={} has been removed from the film ID={}", userId, filmId);
    }

    public void makeUserLike(long filmId, long userId) {
        if (filmsByID.get(filmId).getUsersLikes().contains(userId)) {
            printErrorMessage(String.format(
                    "The user ID=%s has already liked the film with ID=%s", userId, filmId
            ));
        }

        filmsByID.get(filmId).getUsersLikes().add(userId);
        log.info("The user ID={} has liked the film with ID={}", userId, filmId);
    }

    public void checkFilmForExist(long id, String message) {
        if (!filmsByID.containsKey(id)) {
            printErrorMessage(message + id);
        }
    }

    public void printErrorMessage(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    public void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DAY_RELEASE)) {
            log.error("The release date of movie can't earlier than the first day " +
                    "of the Lumiere brothers' release of 1895.12.28");

            throw new ValidationException("The release date of movie can't earlier than the first day " +
                    "of the Lumiere brothers' release of 1895.12.28");
        }

        if (film.getName().isBlank()) {
            log.error("The movie's name can't be empty.");

            throw new ValidationException("The movie's name can't be empty.");
        }
        if (film.getDescription().trim().length() > MAX_LENGTH_DESCRIPTION) {
            log.error("The length of movie description can't be more than 200 characters.");

            throw new ValidationException("The length of movie description can't be more than 200 characters.");
        }
        if (film.getDuration() <= 0) {
            log.error("The movie's duration should be greater zero.");

            throw new ValidationException("The movie's duration should be greater zero.");
        }
    }

    public Long setFilmId() {
        return ++counter;
    }
}
