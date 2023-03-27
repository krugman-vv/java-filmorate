package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
                .email("test@test.ru")
                .login("login")
                .birthday(LocalDate.of(2000, 12, 01))
                .build();
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsEmpty() {
        user.setEmail("");

        Executable executable = () -> controller.create(user);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The email address can't be empty.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenNewUserHasFailEmail() {
        user.setEmail("test.ru");

        Executable executable = () -> controller.create(user);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The email address must contain the symbol \"@\"", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenNewUserHasEmptyLogin() {
        user.setLogin("     ");

        Executable executable = () -> controller.create(user);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The user's login can't be empty", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenNewUserHasLoginWithSpace() {
        user.setLogin("log in");

        Executable executable = () -> controller.create(user);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The user's login can't contain the space.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenNewUserHasBirthdayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));

        Executable executable = () -> controller.create(user);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("The user's birthdate can't be in the future.", exception.getMessage());
    }

    @Test
    public void shouldSetNameEqualLoginWhenNameIsNull() {
        controller.create(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void shouldSetNameEqualLoginWhenNameIsEmpty() {
        user.setName("  ");
        controller.create(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void shouldAddNewUser() {
        controller.create(user);

        assertEquals(user, controller.getUsers().get(user.getId()));
    }

    @Test
    public void shoulUpdateExistingUserWhenUserIdExist() {
        controller.create(user);
        User userUpdate = User.builder()
                .id(user.getId())
                .email("update@test.ru")
                .login("loginUpdate")
                .birthday(LocalDate.of(2010, 01, 20))
                .build();

        controller.update(userUpdate);
        assertEquals(userUpdate, controller.getUsers().get(userUpdate.getId()));
    }

    @Test
    public void shouldThrowExceptionWhenUnknownUserIDUpdateAndEmailOfExistingUser() {
        controller.create(user);
        User userUpdate = User.builder()
                .id(1111)
                .email("test@test.ru")
                .login("loginUpdate")
                .birthday(LocalDate.of(2010, 01, 20))
                .build();

        Executable executable = () -> controller.update(userUpdate);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("Invalid incoming user's ID during updating an existing user:\n" + userUpdate, exception.getMessage());
    }

    @Test
    public void shouldAddUnknownUserIdAndUnknownUserEmailWhenCallUpdateUser() {
        controller.create(user);
        User newUser = User.builder()
                .id(1111)
                .email("unknown@unknown.ru")
                .login("loginUpdate")
                .birthday(LocalDate.of(2010, 01, 20))
                .build();

        Executable executable = () -> controller.update(newUser);

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                executable
        );

        assertEquals("Updating is not possible. The requested user was not found:\n" + newUser, exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenAddNewUserAndEmailAlreadyExist() {
        controller.create(user);
        User newUser = User.builder()
                .email("test@test.ru")
                .login("loginUpdate")
                .birthday(LocalDate.of(2010, 01, 20))
                .build();

        Executable executable = () -> controller.create(newUser);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                executable
        );

        assertEquals("User with such " + newUser.getEmail() + " already exists", exception.getMessage());
    }

    @Test
    public void shouldUpdateExistingUserWhenIdAndEmailExist() {
        controller.create(user);
        User userUpdate = User.builder()
                .id(user.getId())
                .email("test@test.ru")
                .login("loginUpdate")
                .birthday(LocalDate.of(2010, 01, 20))
                .build();

        controller.update(userUpdate);

        assertEquals(userUpdate, controller.getUsers().get(userUpdate.getId()));
    }

    @Test
    public void shouldUpdateEmailOfExistingUserWhenIdExistAndEmailNotExist() {
        controller.create(user);
        User userUpdate = User.builder()
                .id(user.getId())
                .email("notexist@test.ru")
                .login("loginUpdate")
                .birthday(LocalDate.of(2010, 01, 20))
                .build();

        controller.update(userUpdate);

        assertEquals(userUpdate.getEmail(), controller.getUsers().get(user.getId()).getEmail());
    }
}