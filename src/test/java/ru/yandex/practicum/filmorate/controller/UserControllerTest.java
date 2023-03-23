package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserController controller;
    private User user;

    @BeforeEach
    public void beforeEach() {
        controller = new UserController();
        user = User.builder()
                .name("name")
                .email("test@test.ru")
                .login("login")
                .birthday(LocalDate.of(2000, 12, 01))
                .build();
    }

    @Test
    void create() {

//        User user = new User("test@test.ru", "login", LocalDate.of(2000, 12, 01));
        controller.create(user);
        assertEquals(1, user.getId());
    }

    @Test
    void update() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void validateUser() {
    }

    @Test
    void setUserId() {
    }
}