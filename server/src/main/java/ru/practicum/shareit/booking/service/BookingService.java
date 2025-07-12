package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto addBooking(NewBookingDto bookingDto, Long userId);

    BookingDto confirmBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    Collection<BookingDto> getUserBookings(BookingState state, Long userId);

    Collection<BookingDto> getBookingsByOwner(BookingState state, Long userId);

}
