package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemReturnedDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final User user = new User(1L, "John", "john@example.com");
    private final LocalDateTime created = LocalDateTime.now();

    @Test
    void newToItemRequest_shouldMapAllFields() {
        NewItemRequestDto dto = new NewItemRequestDto("Need a drill");
        ItemRequest request = ItemRequestMapper.newToItemRequest(dto);

        assertNull(request.getId());
        assertEquals(dto.getDescription(), request.getDescription());
        assertNull(request.getRequestor());
        assertNotNull(request.getCreated());
    }

    @Test
    void toItemRequestDto_shouldMapAllFields() {
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, created);
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getRequestor(), dto.getRequestor());
        assertEquals(request.getCreated(), dto.getCreated());
    }

    @Test
    void toResponseDto_shouldMapAllFields() {
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, created);
        ResponseItemRequestDto dto = ItemRequestMapper.toResponseDto(request);
        dto.setItems(List.of(new ItemReturnedDto(1L, "Drill", 2L)));

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getRequestor(), dto.getRequestor());
        assertEquals(request.getCreated(), dto.getCreated());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void toResponseDto_withNullItems_shouldReturnDtoWithNullItems() {
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, created);
        ResponseItemRequestDto dto = ItemRequestMapper.toResponseDto(request);

        assertNotNull(dto);
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void newToItemRequest_withNullDto_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            ItemRequestMapper.newToItemRequest(null);
        });
    }

    @Test
    void toResponseDto_withNullRequest_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            ItemRequestMapper.toResponseDto(null);
        });
    }

    @Test
    void toItemRequestDto_withNullRequest_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            ItemRequestMapper.toItemRequestDto(null);
        });
    }
}