package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User editUser(User user, Long userId);

    Optional<User> getUser(Long userId);

    boolean deleteUser(Long userId);

    Collection<User> getUsers();
}
