package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;


public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto editUser(UpdateUserRequest userDto, Long userId);

    UserDto getUser(Long userId);

    boolean deleteUser(Long userId);
}
