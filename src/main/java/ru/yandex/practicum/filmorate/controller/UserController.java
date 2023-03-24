package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final Map<Integer, User> usersById = new HashMap<>();
    private static int counter = 0;

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("POST request received: {}", user);
        validateUser(user);

        if (usersByEmail.containsKey(user.getEmail())) {
            log.error("User with such {} already exists", user.getEmail());
            throw new ValidationException("User with such " + user.getEmail() + " already exists");
        }

        user.setId(setUserId());
        usersByEmail.put(user.getEmail(), user);
        usersById.put(user.getId(), user);

        log.info("A new user has been created: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("PUT request received: {}", user);

        validateUser(user);

        if (!usersById.containsKey(user.getId()) && !usersByEmail.containsKey(user.getEmail())) {
            usersById.put(user.getId(), user);
            usersByEmail.put(user.getEmail(), user);
            log.info("A new user has been created: {}", user);
        } else if (usersById.containsKey(user.getId()) && usersByEmail.containsKey(user.getEmail())) {
            User userBeforeUpdate = usersById.get(user.getId());
            usersById.put(user.getId(), user);
            usersByEmail.put(user.getEmail(), user);

            log.info("The profile of an existing user has been updated.\nBefore: {}\nAfter: {}", userBeforeUpdate, user);
        } else if (usersById.containsKey(user.getId())) {
            String emailBeforeUpdate = usersById.get(user.getId()).getEmail();
            usersByEmail.remove(emailBeforeUpdate);

            usersById.put(user.getId(), user);
            usersByEmail.put(user.getEmail(), user);
            log.info("The email address of an existing user has been updated.\nBefore: {}\nAfter: {}",
                    emailBeforeUpdate, user.getEmail());

        } else {
            log.error("Invalid incoming user's ID={} during updating an existing user ({}).", user.getId(), user.getEmail());

            throw new ValidationException("Invalid incoming user's ID during updating an existing user:\n" + user);
        }

        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return usersByEmail.values();
    }

    public void validateUser(User user) {
        if (user.getEmail().isBlank()) {
            log.error("The email address can't be empty.");
            throw new ValidationException("The email address can't be empty.");
        }
        if (!user.getEmail().contains("@")) {
            log.error("The email address must contain the symbol \"@\"");
            throw new ValidationException("The email address must contain the symbol \"@\"");
        }
        if (user.getLogin().isBlank()) {
            log.error("The user's login can't be empty");
            throw new ValidationException("The user's login can't be empty");
        }
        if (user.getLogin().contains(" ")) {
            log.error("The user's login can't contain the space.");
            throw new ValidationException("The user's login can't contain the space.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("The user's birthdate can't be in the future.");
            throw new ValidationException("The user's birthdate can't be in the future.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("User with id ={} has empty name. The name is set equal login: {}",
                    user.getId(), user.getLogin());
        }
    }

    public int setUserId() {
        return ++counter;
    }

    public HashMap<Integer, User> getUsers() {
        return new HashMap<>(usersById);
    }
}
