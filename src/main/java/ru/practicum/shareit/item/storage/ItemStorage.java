package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item item, User user);

    Item editItem(Item item);

    Optional<Item> getItem(Long itemId);

    Collection<Item> getItems(Long userId);

    Collection<Item> searchItems(String text);
}
