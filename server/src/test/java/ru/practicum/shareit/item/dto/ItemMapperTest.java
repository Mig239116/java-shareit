package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        Item item = new Item(1L, "Item", "Description", true,
                new User(1L, "Owner", "owner@email.com"),
                null, new ArrayList<>(), new ArrayList<>());

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(item.getOwner().getId(), dto.getOwner().getId());
        assertNotNull(dto.getComments());
    }

    @Test
    void toItemReturnedDto() {
        Item item = new Item(1L, "Item", "Description", true,
                new User(1L, "Owner", "owner@email.com"),
                null, new ArrayList<>(), new ArrayList<>());

        ItemReturnedDto dto = ItemMapper.toItemReturnedDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getOwner().getId(), dto.getOwnerId());
    }

    @Test
    void toItemFromItemDto() {
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true,
                new User(1L, "Owner", "owner@email.com"), null, null, null, null);

        Item item = ItemMapper.toItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getOwner().getId(), item.getOwner().getId());
        assertNotNull(item.getComments());
    }

    @Test
    void toItemFromNewItemDto() {
        NewItemDto newItemDto = new NewItemDto("Item", "Description", true, null);

        Item item = ItemMapper.toItem(newItemDto);

        assertNull(item.getId());
        assertEquals(newItemDto.getName(), item.getName());
        assertEquals(newItemDto.getDescription(), item.getDescription());
        assertEquals(newItemDto.getAvailable(), item.getAvailable());
        assertNull(item.getOwner());
        assertNotNull(item.getComments());
    }
}