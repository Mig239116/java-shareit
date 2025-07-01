package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(@Qualifier("bookingServiceImpl") BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@Valid @RequestBody NewBookingDto bookingDto,
                                 @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("POST/bookings - adding new booking by user {} for item {}",
                userId,
                bookingDto.getItemId());
        BookingDto bookingDtoStored = bookingService.addBooking(bookingDto, userId);
        log.debug("POST/booking: the process was completed successfully. A new booking {} for item with id {} has been created by user {}",
                bookingDtoStored.getId(),
                bookingDtoStored.getItem().getId(),
                bookingDtoStored.getBooker().getId()
        );
        return bookingDtoStored;
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto confirmBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.debug("PATCH/booking/id - confirming booking {} by user {}",
                bookingId,
                userId);
        BookingDto bookingDtoStored = bookingService.confirmBooking(bookingId, userId, approved);
        log.debug("PATCH/booking/id: the process was completed successfully. Booking {} was {} by user {}",
                bookingDtoStored.getId(),
                approved,
                userId
        );
        return bookingDtoStored;
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET/bookings/id: returning booking {} requsted by {}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getUserBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingService.getUserBookings(bookingState, userId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByOwner(
            @RequestParam(required = false) String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingService.getBookingsByOwner(bookingState, userId);
    }
}
