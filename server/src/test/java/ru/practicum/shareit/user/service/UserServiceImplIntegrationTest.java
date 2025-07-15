package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotUniqueEmail;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EntityManager em;

    @Test
    void addUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto savedUser = userService.addUser(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertNotNull(user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(savedUser.getId(), user.getId());
    }

    @Test
    void addUserWithDuplicateEmailShouldThrowException() {
        UserDto userDto1 = new UserDto(null, "John", "john@example.com");
        userService.addUser(userDto1);

        UserDto userDto2 = new UserDto(null, "Jane", "john@example.com");

        assertThrows(NotUniqueEmail.class, () -> userService.addUser(userDto2));
    }

    @Test
    void editUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto savedUser = userService.addUser(userDto);

        UserDto updatedUser = userService.editUser(
                new UpdateUserRequest(savedUser.getId(), "John Updated", "john.updated@example.com"),
                savedUser.getId()
        );

        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("John Updated", updatedUser.getName());
        assertEquals("john.updated@example.com", updatedUser.getEmail());
    }

    @Test
    void getUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto savedUser = userService.addUser(userDto);

        UserDto foundUser = userService.getUser(savedUser.getId());

        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getName(), foundUser.getName());
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void deleteUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto savedUser = userService.addUser(userDto);

        userService.deleteUser(savedUser.getId());

        TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.id = :id", Long.class);
        Long count = query.setParameter("id", savedUser.getId()).getSingleResult();

        assertEquals(0, count);
    }
}