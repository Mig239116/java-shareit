package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userServiceImpl") UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.debug("POST/users - adding new user {}",
                userDto.getName());
        UserDto userDtoStored = userService.addUser(userDto);
        log.debug("POST/user: the process was completed successfully. A new user {} with id {} has been created",
                userDtoStored.getName(),
                userDtoStored.getId()
        );
        return userDtoStored;
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto editUser(@RequestBody UpdateUserRequest userDto,
                            @PathVariable Long userId) {
        log.debug("PATCH/users/id - updating user {} with id {}",
                userDto.getName(),
                userId
        );
        UserDto userDtoStored = userService.editUser(userDto, userId);
        log.debug("PATCH/user/id: the process was completed successfully. A new user {} with id {} has been created",
                userDtoStored.getName(),
                userDtoStored.getId()
        );
        return userDtoStored;
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("GET/users/id: returning user {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        log.debug("DELETE/users/id: deleting user {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("result",String.format("User with id %s has been deleted successfully.", userId)));
    }
}
