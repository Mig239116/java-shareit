package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
        ItemDto addItem(NewItemDto itemDto, Long userId);

        ItemDto editItem(UpdateItemRequest itemDto, Long itemId, Long userId);

        ItemDto getItem(Long itemId);

        Collection<ItemDto> getItems(Long userId);

        Collection<ItemDto> searchItems(String text);

        CommentDto addComment(Long itemId, Long userId, CommentRequestDto commentDto);
}
