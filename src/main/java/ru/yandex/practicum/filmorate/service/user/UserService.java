package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        return userStorage.update(user);
    }

    public User delete(User user) {
        return userStorage.delete(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public User addToFriend(long userId, long friendId) {
        return userStorage.addToFriend(userId, friendId);
    }

    public User deleteFromFriends(long userId, long friendId) {
        return userStorage.deleteFromFriends(userId, friendId);
    }

    public Collection<User> getAllUserFriends(long userId) {
        return userStorage.getAllUserFriends(userId);
    }

    public Collection<User> getMutualFriends(long firstUserId, long secondUserId) {
        return userStorage.getMutualFriends(firstUserId, secondUserId);
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("The user's login can't contain the space.");
            throw new ValidationException("The user's login can't contain the space.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("User with id ={} has empty name. The name is set equal login: {}",
                    user.getId(), user.getLogin());
        }
    }
}
