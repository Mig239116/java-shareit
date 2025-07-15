package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.BadRequestException;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}", stateParam, userId);
        return bookingClient.getUserBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        validateDates(requestDto);
        return bookingClient.addBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBooking(@PathVariable Long bookingId,
                                                 @RequestParam Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId

    ) {
        log.info("Approving booking {}, userId={}", bookingId, userId);
        return bookingClient.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestParam(required = false) String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state {}, userId={}", state, userId);
        return bookingClient.getBookingsByOwner(userId, state);
    }

    private void validateDates(BookItemRequestDto booking) {
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new BadRequestException("The start date " +
                    booking.getStart() + " is after or equals to end date " +
                    booking.getEnd());
        }
    }
}