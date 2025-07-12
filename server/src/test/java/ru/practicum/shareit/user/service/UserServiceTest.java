package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmail;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addUser_ShouldSaveUser_WhenEmailIsUnique() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        User savedUser = UserMapper.toUser(userDto);
        savedUser.setId(1L);

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.addUser(userDto);

        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_ShouldThrowException_WhenEmailExists() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(NotUniqueEmail.class, () -> userService.addUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void editUser_ShouldUpdateUser_WhenDataValid() {
        Long userId = 1L;
        User existingUser = new User(userId, "John", "john@example.com");
        UpdateUserRequest updateRequest = new UpdateUserRequest(userId, "John Updated", "john.updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);

        UserDto result = userService.editUser(updateRequest, userId);

        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
        // Не проверяем save() так как его нет в оригинальном коде
    }

    @Test
    void editUser_ShouldNotCheckEmail_WhenEmailNotChanged() {
        Long userId = 1L;
        User existingUser = new User(userId, "John", "john@example.com");
        UpdateUserRequest updateRequest = new UpdateUserRequest(userId, "John Updated", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDto result = userService.editUser(updateRequest, userId);

        assertEquals("John Updated", result.getName());
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void editUser_ShouldThrowException_WhenEmailExists() {
        Long userId = 1L;
        User existingUser = new User(userId, "John", "john@example.com");
        UpdateUserRequest updateRequest = new UpdateUserRequest(userId, "John Updated", "existing@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(true);

        assertThrows(NotUniqueEmail.class, () -> userService.editUser(updateRequest, userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void editUser_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;
        UpdateUserRequest updateRequest = new UpdateUserRequest(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.editUser(updateRequest, userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUser_ShouldReturnUser_WhenExists() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(userId);

        assertEquals(userId, result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUser_ShouldThrowException_WhenNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenExists() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }
}