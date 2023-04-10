package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static long counter = 0;
    private final Map<Long, Film> filmsByID = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(setFilmId());
        film.setLikes(new HashSet<>());
        filmsByID.put(film.getId(), film);

        log.info("A new film has been added: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        long filmId = film.getId();
        checkFilmForExist(
                filmId,
                "Updating a movie is not possible. The movie is not found, ID="
        );

        film.setLikes(filmsByID.get(filmId).getLikes());
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
    public Film addLike(long filmId, long userId) {
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
                    int comp = compare(o1.getLikes().size(), o2.getLikes().size());
                    return -1 * comp;
                }).limit(count)
                .collect(Collectors.toList());
    }

    private void unlike(long filmId, long userId) {
        Set<Long> usersLikes = filmsByID.get(filmId).getLikes();

        if (!usersLikes.contains(userId)) {
            printErrorMessage(String.format(
                    "The user ID=%s didn't like the film with ID=%s", userId, filmId
            ));
        }

        usersLikes.remove(userId);
        log.info("The like of user with ID={} has been removed from the film ID={}", userId, filmId);
    }

    private void makeUserLike(long filmId, long userId) {
        Set<Long> usersLikes = filmsByID.get(filmId).getLikes();

        if (usersLikes.contains(userId)) {
            printErrorMessage(String.format(
                    "The user ID=%s has already liked the film with ID=%s", userId, filmId
            ));
        }

        usersLikes.add(userId);
        log.info("The user ID={} has liked the film with ID={}", userId, filmId);
    }

    private void checkFilmForExist(long id, String message) {
        if (!filmsByID.containsKey(id)) {
            printErrorMessage(message + id);
        }
    }

    private void printErrorMessage(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    private Long setFilmId() {
        return ++counter;
    }
}
