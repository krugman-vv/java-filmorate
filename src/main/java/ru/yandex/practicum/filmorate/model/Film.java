package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private int duration;
}
