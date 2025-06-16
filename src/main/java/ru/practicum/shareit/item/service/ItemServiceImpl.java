package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserServiceImpl userService;

    @Autowired
    public ItemServiceImpl(@Qualifier("itemStorageImpl") ItemStorage itemStorage,
                           UserServiceImpl userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User user = userService.validateNotFound(userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemStorage.addItem(item, user));
    }

    @Override
    public ItemDto editItem(UpdateItemRequest itemDto, Long itemId, Long userId) {
        userService.validateNotFound(userId);
        Item item = validateNotFound(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User is not the owner of the item");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.editItem(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(validateNotFound(itemId));
    }

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        return itemStorage.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item validateNotFound(Long id) {
        return itemStorage.getItem(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("Item " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }
}
