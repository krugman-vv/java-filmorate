package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import static java.time.Month.DECEMBER;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static int counter = 0;
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate MIN_DAY_RELEASE = LocalDate.of(1895, DECEMBER, 28);
    private final Map<Integer, Film> filmsByID = new HashMap<>();
    private final Set<String> filmsName = new HashSet<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("POST request received: {}", film);

        validateFilm(film);

        if (filmsName.contains(film.getName())) {
            throw new ValidationException("This movie's name has already been added to the collection.");
        }

        film.setId(setFilmId());
        filmsName.add(film.getName());
        filmsByID.put(film.getId(), film);

        log.info("A new film has been created: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("PUT request received: {}", film);

        validateFilm(film);

        if (!filmsByID.containsKey(film.getId()) && !filmsName.contains(film.getName())) {
            filmsByID.put(film.getId(), film);
            filmsName.add(film.getName());

            log.info("A new film has been created: {}", film);
        } else if (filmsByID.containsKey(film.getId()) && filmsName.contains(film.getName())) {
            Film filmBeforeUpdate = filmsByID.get(film.getId());
            filmsByID.put(film.getId(), film);
            filmsName.add(film.getName());

            log.info("The profile of en existing film has been updated.\nBefore: {}\nAfter:{}", filmBeforeUpdate, film);
        } else if (filmsByID.containsKey(film.getId())) {
            String nameBeforeUpdate = filmsByID.get(film.getId()).getName();
            filmsName.remove(nameBeforeUpdate);

            filmsByID.put(film.getId(), film);
            filmsName.add(film.getName());

            log.info("The name of en existing movie has been updated.\nBefore: {}.\nAfter: {}", nameBeforeUpdate, film.getName());

        } else {
            log.error("Invalid incoming movie's ID={} during updating an existing movie's name ({})", film.getId(), film.getName());

            throw new ValidationException("Invalid incoming movie's ID during updating an existing movie's name " + film.getName());
        }

        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmsByID.values();
    }

    public void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            log.error("The movie's name can't be empty.");

            throw new ValidationException("The movie's name can't be empty.");
        }
        if (film.getDescription().trim().length() > MAX_LENGTH_DESCRIPTION) {
            log.error("The length of movie description can't be more than 200 characters.");

            throw new ValidationException("The length of movie description can't be more than 200 characters.");
        }
        if (film.getReleaseDate().isBefore(MIN_DAY_RELEASE)) {
            log.error("The release date of movie can't earlier than the first day " +
                    "of the Lumiere brothers' release of 1895.12.28");

            throw new ValidationException("The release date of movie can't earlier than the first day " +
                    "of the Lumiere brothers' release of 1895.12.28");
        }
        if (film.getDuration() <= 0) {
            log.error("The movie's duration should be greater zero.");

            throw new ValidationException("The movie's duration should be greater zero.");
        }
    }

    public int setFilmId() {
        return ++counter;
    }

    public HashMap<Integer, Film> getFilms() {
        return new HashMap<>(filmsByID);
    }
}
