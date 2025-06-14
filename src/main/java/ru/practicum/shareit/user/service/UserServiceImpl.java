package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmail;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    public final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userStorageImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (validateEmail(userDto.getEmail()) > 0) {
            throw new NotUniqueEmail("Email " + userDto.getEmail() + " has been used");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.addUser(user));
    }

    @Override
    public UserDto editUser(UpdateUserRequest userDto, Long userId) {
        User user = validateNotFound(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && userDto.getEmail() != user.getEmail()) {
            if (validateEmail(userDto.getEmail()) > 0) {
                throw new NotUniqueEmail("Email " + userDto.getEmail() + " has been used");
            }
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userStorage.editUser(user, userId));
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(validateNotFound(userId));
    }

    @Override
    public boolean deleteUser(Long userId) {
        validateNotFound(userId);
        return userStorage.deleteUser(userId);
    }

    public User validateNotFound(Long id) {
        return userStorage.getUser(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("User " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }

    private long validateEmail(String email) {
        return userStorage.getUsers()
                .stream()
                .filter(user -> user.getEmail().contains(email))
                .count();
    }

}
