package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Film delete(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    Film addUserLike(long filmId, long userId);

    Film deleteLike(long filmId, long userId);

    Collection<Film> getTopFilms(int count);
}
