package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("POST/users - adding new user {}",
                userDto.getName());
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> editUser(@Valid @RequestBody UpdateUserRequest userDto,
                            @PathVariable Long userId) {
        log.debug("PATCH/users/id - updating user {} with id {}",
                userDto.getName(),
                userId
        );
        return userClient.editUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.debug("GET/users/id: returning user {}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.debug("DELETE/users/id: deleting user {}", userId);
        return userClient.deleteUser(userId);
    }
}
