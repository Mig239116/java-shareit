package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) > 0 FROM Booking b JOIN b.item i WHERE b.id = :bookingId AND i.owner.id = :ownerId")
    boolean existsByIdAndOwnerId(@Param("bookingId") Long bookingId,
                                 @Param("ownerId") Long ownerId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.id = :bookingId " +
            "AND (b.booker.id = :userId OR b.item.owner.id = :userId)")
    boolean isBookingOwnerOrBooker(@Param("bookingId") Long bookingId,
                                   @Param("userId") Long userId);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(
            Long bookerId,
            BookingStatus status,
            Sort sort
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :currentTime " +
            "AND b.end >= :currentTime ")
    List<Booking> findCurrentBookings(
            @Param("bookerId") Long bookerId,
            @Param("currentTime") LocalDateTime currentTime,
            Sort sort
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :currentTime ")
    List<Booking> findPastBookings(
            @Param("bookerId") Long bookerId,
            @Param("currentTime") LocalDateTime currentTime,
            Sort sort
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :currentTime ")
    List<Booking> findFutureBookings(
            @Param("bookerId") Long bookerId,
            @Param("currentTime") LocalDateTime currentTime,
            Sort sort
    );

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByOwner(
            @Param("ownerId") Long ownerId,
            @Param("now") LocalDateTime now,
            Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now")
    List<Booking> findPastByOwner(
            @Param("ownerId") Long ownerId,
            @Param("now") LocalDateTime now,
            Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now")
    List<Booking> findFutureByOwner(
            @Param("ownerId") Long ownerId,
            @Param("now") LocalDateTime now,
            Sort sort);

    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);

    @Query("SELECT b.end FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :currentTime " +
            "ORDER BY b.end DESC")
    List<LocalDateTime> findLastBookingDate(@Param("itemId") Long itemId,
                                            @Param("currentTime") LocalDateTime currentTime,
                                            Sort sort);

    @Query("SELECT b.start FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start ASC")
    List<LocalDateTime> findNextBookingDate(@Param("itemId") Long itemId,
                                            @Param("currentTime") LocalDateTime currentTime,
                                            Sort sort);
}
