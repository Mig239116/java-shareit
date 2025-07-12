package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserService userService;

    private Long userId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");
        UserDto savedUser = userService.addUser(userDto);
        userId = savedUser.getId();

        NewItemDto itemDto = new NewItemDto("Test Item", "Test Description", true, null);
        ItemDto savedItem = itemService.addItem(itemDto, userId);
        itemId = savedItem.getId();
    }

    @Test
    void addItem_shouldAddItemSuccessfully() {
        NewItemDto newItemDto = new NewItemDto("New Item", "New Description", true, null);

        ItemDto result = itemService.addItem(newItemDto, userId);

        assertNotNull(result.getId());
        assertEquals("New Item", result.getName());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void editItem_shouldUpdateItemFields() {
        UpdateItemRequest updateRequest = new UpdateItemRequest("Updated Name", "Updated Desc", false);

        ItemDto result = itemService.editItem(updateRequest, itemId, userId);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Desc", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void editItem_whenUserNotOwner_shouldThrowException() {
        UpdateItemRequest updateRequest = new UpdateItemRequest("Updated", "Desc", true);

        assertThrows(NotFoundException.class,
                () -> itemService.editItem(updateRequest, itemId, userId + 1));
    }

    @Test
    void getItem_shouldReturnItem() {
        ItemDto result = itemService.getItem(itemId);

        assertEquals(itemId, result.getId());
        assertEquals("Test Item", result.getName());
    }

    @Test
    void getItems_shouldReturnUserItems() {
        Collection<ItemDto> items = itemService.getItems(userId);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
    }

    @Test
    void searchItems_shouldFindByText() {
        Collection<ItemDto> results = itemService.searchItems("test");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void addComment_whenUserDidNotBookItem_shouldThrowException() {
        assertThrows(BadRequestException.class,
                () -> itemService.addComment(itemId, userId,
                        new CommentRequestDto("Test comment")));
    }
}