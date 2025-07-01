package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmail;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    public final UserRepository userStorage;


    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
    @Transactional
    public UserDto editUser(UpdateUserRequest userDto, Long userId) {
        User user = validateNotFound(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            validateEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(validateNotFound(userId));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        validateNotFound(userId);
        userStorage.deleteById(userId);
    }

    private User validateNotFound(Long id) {
        return userStorage.findById(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("User " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }

    private void validateEmail(String email) {
        if (userStorage.existsByEmail(email)) {
            throw new NotUniqueEmail("Email " + email + " already exists");
        }
    }

}
