package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingStorage;
    private final ItemServiceImpl itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(NewBookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Item with id " + item.getId() + " is not available for booking");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingStorage.save(booking));
    }

    @Override
    @Transactional
    public BookingDto confirmBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = validateNotFound(bookingId);
        validateBookingByOwner(bookingId, userId);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        BookingDto bookingDto = BookingMapper.toBookingDto(validateNotFound(bookingId));
        validateBookingByBookerOrOwner(bookingId, userId);
        return bookingDto;
    }

    @Override
    public Collection<BookingDto> getUserBookings(BookingState state, Long userId) {
        if (state == null) {
            state = BookingState.ALL;
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Sort sort = Sort.by("start").descending();
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingStorage.findByBookerId(userId, sort);
            case CURRENT -> bookingStorage.findCurrentBookings(userId, now, sort);
            case PAST -> bookingStorage.findPastBookings(userId, now, sort);
            case FUTURE -> bookingStorage.findFutureBookings(userId, now, sort);
            case WAITING -> bookingStorage.findByBookerIdAndStatus(
                    userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingStorage.findByBookerIdAndStatus(
                    userId, BookingStatus.REJECTED, sort);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    public Collection<BookingDto> getBookingsByOwner(BookingState state, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        long count = itemService.getItems(userId).size();
        if (count == 0) {
            throw new NotFoundException("User has no items");
        }
        Sort sort = Sort.by("start").descending();
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingStorage.findByItemOwnerId(userId, sort);
            case CURRENT -> bookingStorage.findCurrentByOwner(userId, now, sort);
            case PAST -> bookingStorage.findPastByOwner(userId, now, sort);
            case FUTURE -> bookingStorage.findFutureByOwner(userId, now, sort);
            case WAITING -> bookingStorage.findByItemOwnerIdAndStatus(
                    userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingStorage.findByItemOwnerIdAndStatus(
                    userId, BookingStatus.REJECTED, sort);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validateBookingByBookerOrOwner(Long bookingId, Long userId) {
        if (!bookingStorage.isBookingOwnerOrBooker(bookingId, userId)) {
            throw new NotFoundException("The requestor is not the owner or booker");
        }
    }

    private void validateBookingByOwner(Long bookingId, Long ownerId) {
        if (!bookingStorage.existsByIdAndOwnerId(bookingId, ownerId)) {
            throw new BadRequestException("The initiator is not the owner of the booking");
        }
    }

    private Booking validateNotFound(Long id) {
        return bookingStorage.findById(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("Booking " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }

}
