package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStorage;
    private final UserServiceImpl userService;
    private final UserRepository userStorage;
    private final CommentRepository commentStorage;
    private final BookingRepository bookingStorage;


    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDto editItem(UpdateItemRequest itemDto, Long itemId, Long userId) {
        userService.getUser(userId);
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
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(validateNotFound(itemId));
    }

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        Collection<Item> items = itemStorage.findByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();
        return items.stream().map(item -> {
            ItemDto dto = ItemMapper.toItemDto(item);

            List<LocalDateTime> lastDates = bookingStorage.findLastBookingDate(
                    item.getId(), now, Sort.by(Sort.Direction.DESC, "end"));
            if (!lastDates.isEmpty()) {
                dto.setLastBooking(lastDates.get(0).toLocalDate());
            }

            List<LocalDateTime> nextDates = bookingStorage.findNextBookingDate(
                    item.getId(), now, Sort.by(Sort.Direction.ASC, "start"));
            if (!nextDates.isEmpty()) {
                dto.setNextBooking(nextDates.get(0).toLocalDate());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentRequestDto commentDto) {
        User author = validateUser(userId);
        Item item = validateNotFound(itemId);
        if (!bookingStorage.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now())) {
            throw new BadRequestException("User didn't book this item");
        }

        Comment comment = CommentMapper.requestToComment(commentDto, item, author);
        Comment savedComment = commentStorage.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    private User validateUser(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    private Item validateNotFound(Long id) {
        return itemStorage.findById(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("Item " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }
}
