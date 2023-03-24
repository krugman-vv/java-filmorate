package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController controller;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController();
        film = Film.builder()
                .name("movie")
                .description("this is new movie")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(100)
                .build();
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        film.setName("   ");

        Executable executable = () -> controller.create(film);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The movie's name can't be empty.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenDescriptionHasLengthOver200() {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");

        Executable executable = () -> controller.create(film);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The length of movie description can't be more than 200 characters.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenReleaseDateBeforeMinReleaseDate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        Executable executable = () -> controller.create(film);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The release date of movie can't earlier than the first day of the Lumiere brothers' release of 1895.12.28", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenDurationLessOrEqualsZero() {
        film.setDuration(0);
        Executable executable = () -> controller.create(film);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The movie's duration should be greater zero.", exception.getMessage());
    }

    @Test
    public void shouldAddNewMovie() {
        controller.create(film);

        assertEquals(film, controller.getFilms().get(film.getId()));
    }

    @Test
    public void shouldThrowExceptionWhenNewMovieHasNameOfExcistingMovie() {
        controller.create(film);
        Film newFilm = Film.builder()
                .name("movie")
                .description("this is not existing movie")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(100)
                .build();

        Executable executable = () -> controller.create(film);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("This movie's name has already been added to the collection.", exception.getMessage());
    }

    @Test
    public void shouldUpdateExistingMovieWhenIdAndEmailAlreadyExist() {
        controller.create(film);
        Film newFilm = Film.builder()
                .id(film.getId())
                .name("movie")
                .description("this is new movie's description.")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(100)
                .build();

        controller.update(newFilm);

        assertEquals(newFilm, controller.getFilms().get(newFilm.getId()));
    }

    @Test
    public void shouldAddNewMovieWhenCallUpdateAndMovieIdNotExistAndMovieNameNotExist() {
        controller.create(film);
        Film newFilm = Film.builder()
                .id(film.getId() + 1)
                .name("another movie")
                .description("this is another movie's description.")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(100)
                .build();

        controller.update(newFilm);

        assertEquals(2, controller.getFilms().size());
        assertEquals(newFilm, controller.getFilms().get(newFilm.getId()));
    }

    @Test
    public void shouldUpdateExistingMovieWhenIdExistAndNameNotExist() {
        controller.create(film);
        Film newFilm = Film.builder()
                .id(film.getId())
                .name("another movie")
                .description("this is another movie's description.")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(100)
                .build();

        controller.update(newFilm);

        assertEquals(1, controller.getFilms().size());
        assertEquals(newFilm, controller.getFilms().get(newFilm.getId()));
    }

    @Test
    public void shouldThrowExceptionWhenNewMovieNameExistAndNewMovieIdNotExist() {
        controller.create(film);
        Film newFilm = Film.builder()
                .id(111)
                .name("movie")
                .description("this is unknown movie")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(100)
                .build();

        Executable executable = () -> controller.update(newFilm);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("Invalid incoming movie's ID during updating an existing movie's name " + newFilm.getName(), exception.getMessage());
    }
}