package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@email.com");
        booker = new User(null, "Booker", "booker@email.com");
        item = new Item(null, "Item", "Description", true, owner, null, null, null);
        booking = new Booking(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.WAITING);

        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(item);
        entityManager.persist(booking);
        entityManager.flush();
    }

    @Test
    void findByBookerId_ShouldReturnBookings() {
        List<Booking> result = bookingRepository.findByBookerId(booker.getId(),
                Sort.by(Sort.Direction.DESC, "start"));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findByItemOwnerId_ShouldReturnBookings() {
        List<Booking> result = bookingRepository.findByItemOwnerId(owner.getId(),
                Sort.by(Sort.Direction.DESC, "start"));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void existsByIdAndOwnerId_ShouldReturnTrue() {
        boolean exists = bookingRepository.existsByIdAndOwnerId(booking.getId(), owner.getId());

        assertTrue(exists);
    }

    @Test
    void isBookingOwnerOrBooker_ShouldReturnTrue() {
        boolean isOwnerOrBooker = bookingRepository.isBookingOwnerOrBooker(booking.getId(), owner.getId());

        assertTrue(isOwnerOrBooker);
    }
}
