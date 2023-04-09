package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

import static java.time.Month.DECEMBER;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate MIN_DAY_RELEASE = LocalDate.of(1895, DECEMBER, 28);

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        return filmStorage.update(film);
    }

    public Film delete(Film film) {
        return filmStorage.delete(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(long filmId, long userId) {
        userService.getUserById(userId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(long filmId, long userId) {
        userService.getUserById(userId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DAY_RELEASE)) {
            log.error("The release date of movie can't earlier than the first day " +
                    "of the Lumiere brothers' release of 1895.12.28");

            throw new ValidationException("The release date of movie can't earlier than the first day " +
                    "of the Lumiere brothers' release of 1895.12.28");
        }

 /*       if (film.getName().isBlank()) {
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
        }*/
    }
}
