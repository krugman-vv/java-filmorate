package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> usersById = new HashMap<>();
    private static long counter = 0;

    @Override
    public User create(User user) {
        user.setId(getUserId());
        user.setFriends(new HashSet<>());
        usersById.put(user.getId(), user);

        log.info("A new user has been created: {}", user);

        return user;
    }

    @Override
    public User update(User user) {
        long userId = user.getId();
        checkUserForExist(
                List.of(userId),
                "Updating is not possible. The user was not found: ID=" + userId
        );

        user.setFriends(usersById.get(userId).getFriends());
        usersById.put(userId, user);
        log.info("The user has been updated: {}", user);

        return user;
    }

    @Override
    public User delete(User user) {
        checkUserForExist(
                List.of(user.getId()),
                "Invalid incoming user's ID during request to get delete user by ID="
        );

        usersById.remove(user.getId());
        log.info("The user ID={} has been deleted.", user.getId());

        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return usersById.values();
    }

    @Override
    public User getUserById(long id) {
        checkUserForExist(
                List.of(id),
                "Getting an existing user is not possible. User not found, ID="
        );
        return usersById.get(id);
    }

    @Override
    public User addToFriend(long userId, long friendId) {
        checkUserForExist(
                List.of(userId, friendId),
                "Adding a friend is not possible. User not found, ID="
        );

        makeFriends(userId, friendId);

        return usersById.get(userId);
    }

    @Override
    public User deleteFromFriends(long userId, long friendId) {
        checkUserForExist(
                List.of(userId, friendId),
                "Deleting a friend is not possible. User not found, ID="
        );

        stopBeingFriends(userId, friendId);

        return usersById.get(userId);
    }

    @Override
    public Collection<User> getAllUserFriends(long userId) {
        checkUserForExist(
                List.of(userId),
                "It's not possible to get all user's friends. User not found, ID="
        );

        Set<Long> friends = usersById.get(userId).getFriends();
        return friends.stream()
                .map(usersById::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getMutualFriends(long firstUserId, long secondUserId) {
        checkUserForExist(
                List.of(firstUserId, secondUserId),
                "It's not possible to get mutual friends. User not found, ID="
        );

        Set<Long> firstUserFriends = usersById.get(firstUserId).getFriends();
        Set<Long> secondUserFriends = usersById.get(secondUserId).getFriends();
        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .map(usersById::get)
                .collect(Collectors.toList());
    }

    private void makeFriends(Long userId, Long friendId) {
        if (usersById.get(userId).getFriends().contains(friendId)) {
            printErrorMessage(String.format(
                    "The user ID=%s already friends with the user ID=%s", userId, friendId
            ));
        }

        usersById.get(userId).getFriends().add(friendId);
        usersById.get(friendId).getFriends().add(userId);

        log.info("The user ID={} has become a friend of the user ID={}", friendId, userId);
        log.info("The user ID={} has become a friend of the user ID={}", userId, friendId);
    }

    private void stopBeingFriends(Long userId, Long friendId) {
        usersById.get(userId).getFriends().remove(friendId);
        usersById.get(friendId).getFriends().remove(userId);

        log.info("The user ID={} has been removed from friends of the user ID={}", friendId, userId);
        log.info("The user ID={} has been removed from friends of the user ID={}", userId, friendId);
    }

    private void checkUserForExist(List<Long> users, String message) {
        for (Long id : users) {
            if (!usersById.containsKey(id)) {
                printErrorMessage(message + id);
            }
        }
    }

    private void printErrorMessage(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    private Long getUserId() {
        return ++counter;
    }
}
