package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User create(User user);
    User update(User user);
    User delete(User user);
    Collection<User> getAllUsers();
    User getUserById(long id);
    User addToFriend(long userId, long friendId);
    User deleteFromFriends(long userId, long friendId);
    Collection<User> getAllUserFriends(long userId);
    Collection<User> getMutualFriends(long firstUserId, long secondUserId);
}
