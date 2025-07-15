package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String state) {
        if (state == null) {
            return Optional.of(ALL);
        }
        try {
            return Optional.of(BookingState.valueOf(state.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
