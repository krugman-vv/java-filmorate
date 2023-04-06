package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
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
}
