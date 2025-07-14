package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;
    private NewBookingDto newBookingDto;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@email.com");
        booker = new User(null, "Booker", "booker@email.com");
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        item = new Item(null, "Item", "Description", true, owner, null, null, new ArrayList<>());
        item = itemRepository.save(item);

        newBookingDto = new NewBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
    }

    @Test
    void addBooking_ShouldSaveBookingToDatabase() {
        BookingDto result = bookingService.addBooking(newBookingDto, booker.getId());

        assertNotNull(result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());

        Booking savedBooking = bookingRepository.findById(result.getId()).orElseThrow();
        assertEquals(result.getId(), savedBooking.getId());
    }

    @Test
    void addBooking_WhenItemNotAvailable_ShouldThrowException() {
        item.setAvailable(false);
        itemRepository.save(item);

        assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(newBookingDto, booker.getId()));
    }


    @Test
    void getBooking_ShouldReturnBooking() {
        BookingDto bookingDto = bookingService.addBooking(newBookingDto, booker.getId());

        BookingDto result = bookingService.getBooking(bookingDto.getId(), booker.getId());

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() {
        bookingService.addBooking(newBookingDto, booker.getId());

        List<BookingDto> result = (List<BookingDto>) bookingService.getUserBookings(
                BookingState.ALL, booker.getId());

        assertEquals(1, result.size());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
    }

    @Test
    void getBookingsByOwner_ShouldReturnOwnerBookings() {
        bookingService.addBooking(newBookingDto, booker.getId());

        List<BookingDto> result = (List<BookingDto>) bookingService.getBookingsByOwner(
                BookingState.ALL, owner.getId());

        assertEquals(1, result.size());
        assertEquals(owner.getId(), result.get(0).getItem().getOwner().getId());
    }

    @Test
    void validateDates_WhenEndBeforeStart_ShouldThrowException() {
        NewBookingDto invalidBooking = new NewBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(invalidBooking, booker.getId()));
    }
}