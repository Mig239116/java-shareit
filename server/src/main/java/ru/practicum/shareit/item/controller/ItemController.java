package ru.practicum.shareit.item.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("itemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestBody NewItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("POST/items - adding new item {} by user {}",
                itemDto.getName(),
                userId);
        ItemDto itemDtoStored = itemService.addItem(itemDto, userId);
        log.debug("POST/items: the process was completed successfully. A new item {} with id {} has been created by user {}",
                itemDtoStored.getName(),
                itemDtoStored.getId(),
                itemDtoStored.getOwner()
        );
        return itemDtoStored;
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto editItem(@RequestBody UpdateItemRequest itemDto,
                            @PathVariable Long itemId,
                            @RequestHeader("X-Sharer-User-Id") Long userId
                            ) {
        log.debug("PATCH/item/id - adding new item {} by user {}",
                itemDto.getName(),
                userId);
        ItemDto itemDtoStored = itemService.editItem(itemDto, itemId, userId);
        log.debug("PATCH/item/id: the process was completed successfully. A new item {} with id {} has been created by user {}",
                itemDtoStored.getName(),
                itemDtoStored.getId(),
                itemDtoStored.getOwner()
        );
        return itemDtoStored;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET/items: all items of the user {} returned", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable Long itemId) {
        log.debug("GET/items/id: returning item {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.debug("GET/items: all items of the containing text {}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestBody CommentRequestDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(itemId, userId, commentDto);
    }

}
