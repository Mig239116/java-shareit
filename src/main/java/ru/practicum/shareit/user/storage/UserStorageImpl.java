package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users;
    private Long id;

    public UserStorageImpl() {
        this.users = new HashMap<Long, User>();
        this.id = 0L;
    }

    @Override
    public User addUser(User user) {
        user.setId(this.id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User editUser(User user, Long userId) {
        return users.put(userId, user);
    }

    @Override
    public Optional<User> getUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public boolean deleteUser(Long userId) {
        return users.remove(userId, users.get(userId));
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }
}
