package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items;
    private Long id;

    public ItemStorageImpl() {
        this.items = new HashMap<Long, Item>();
        this.id = 0L;
    }

    @Override
    public Item addItem(Item item, User user) {
        item.setId(this.id++);
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item editItem(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> getItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item);
    }

    @Override
    public Collection<Item> getItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String textLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName() != null && item.getName().toLowerCase().contains(textLowerCase)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(textLowerCase)))
                .collect(Collectors.toList());
    }

}
