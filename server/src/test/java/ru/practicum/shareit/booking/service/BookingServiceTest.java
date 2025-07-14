package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingStorage;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private NewBookingDto newBookingDto;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@email.com");
        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(user)
                .request(null)
                .bookings(Collections.emptyList())
                .comments(Collections.emptyList())
                .build();

        newBookingDto = new NewBookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        booking = Booking.builder()
                .id(1L)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addBooking_ShouldSaveBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(newBookingDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    void addBooking_WhenItemNotAvailable_ShouldThrowException() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(newBookingDto, 1L));
    }

    @Test
    void addBooking_WhenInvalidDates_ShouldThrowException() {
        NewBookingDto invalidBookingDto = new NewBookingDto(
                1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(invalidBookingDto, 1L));
    }

    @Test
    void confirmBooking_WhenApproved_ShouldUpdateStatus() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.existsByIdAndOwnerId(anyLong(), anyLong())).thenReturn(true);

        BookingDto result = bookingService.confirmBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void confirmBooking_WhenNotOwner_ShouldThrowException() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.existsByIdAndOwnerId(anyLong(), anyLong())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> bookingService.confirmBooking(1L, 2L, true));
    }

    @Test
    void confirmBooking_WhenStatusNotWaiting_ShouldThrowException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.existsByIdAndOwnerId(anyLong(), anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.confirmBooking(1L, 1L, true));
    }

    @Test
    void getBooking_ShouldReturnBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.isBookingOwnerOrBooker(anyLong(), anyLong())).thenReturn(true);

        BookingDto result = bookingService.getBooking(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBooking_WhenNotOwnerOrBooker_ShouldThrowException() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.isBookingOwnerOrBooker(anyLong(), anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1L, 2L));
    }

    @Test
    void getUserBookings_ShouldReturnBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.findByBookerId(anyLong(), any(Sort.class))).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getUserBookings(BookingState.ALL, 1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getBookingsByOwner_ShouldReturnBookings() {
        Item testItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(user)
                .request(null)
                .bookings(Collections.emptyList())
                .comments(Collections.emptyList())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemService.getItems(anyLong())).thenReturn(List.of(testItem).stream().map(ItemMapper::toItemDto).toList());
        when(bookingStorage.findByItemOwnerId(anyLong(), any(Sort.class))).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getBookingsByOwner(BookingState.ALL, 1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getBookingsByOwner_WhenNoItems_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemService.getItems(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwner(BookingState.ALL, 1L));
    }

    private User createTestUser(Long id) {
        return new User(id, "User " + id, "user" + id + "@test.com");
    }

    private Item createTestItem(Long id, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("Description " + id);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setBookings(new ArrayList<>());
        item.setComments(new ArrayList<>());
        return item;
    }

    private Booking createTestBooking(Long id, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    @Test
    void getUserBookings_ShouldHandleAllStates() {
        Long userId = 1L;
        User user = createTestUser(userId);
        Item item = createTestItem(1L, createTestUser(2L));
        Booking booking = createTestBooking(1L, user, item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        lenient().when(bookingStorage.findByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        lenient().when(bookingStorage.findCurrentBookings(anyLong(), any(), any())).thenReturn(List.of(booking));
        lenient().when(bookingStorage.findPastBookings(anyLong(), any(), any())).thenReturn(List.of(booking));
        lenient().when(bookingStorage.findFutureBookings(anyLong(), any(), any())).thenReturn(List.of(booking));
        lenient().when(bookingStorage.findByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        for (BookingState state : BookingState.values()) {
            assertDoesNotThrow(() -> {
                Collection<BookingDto> result = bookingService.getUserBookings(state, userId);
                assertNotNull(result);
            });
        }
    }

    @Test
    void getUserBookings_WithUnknownState_ShouldThrowException() {
        assertThrows(NoSuchElementException.class,
                () -> bookingService.getUserBookings(BookingState.from("UNKNOWN").orElseThrow(), user.getId()));
    }

}