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
}