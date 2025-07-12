package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
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
    private final ItemRequestRepository itemRequestRepository;


    @Override
    @Transactional
    public ItemDto addItem(NewItemDto itemDto, Long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = validateRequest(itemDto.getRequestId());
            item.setRequest(request);
        }
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
        List<Item> items = itemStorage.findByOwnerId(userId);
        List<Booking> allBookings = bookingStorage.findApprovedBookingsForItems(items);
        Map<Long, List<Booking>> bookingsByItem = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), Collections.emptyList());

                    Optional<Booking> lastBooking = itemBookings.stream()
                            .filter(b -> b.getEnd().isBefore(now))
                            .max(Comparator.comparing(Booking::getEnd));

                    Optional<Booking> nextBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart));

                    lastBooking.ifPresent(b -> dto.setLastBooking(b.getEnd().toLocalDate()));
                    nextBooking.ifPresent(b -> dto.setNextBooking(b.getStart().toLocalDate()));

                    return dto;
                })
                .collect(Collectors.toList());
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

    private ItemRequest validateRequest(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request " + id + " not found"));
    }

    private User validateUser(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    private Item validateNotFound(Long id) {
        Item item = itemStorage.findById(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("Item " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
        Hibernate.initialize(item.getComments());
        return item;
    }
}
