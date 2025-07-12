package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT item FROM Item item " +
            "WHERE (upper(item.name) LIKE upper(concat('%', :text, '%')) " +
            "OR upper(item.description) LIKE upper(concat('%', :text, '%'))) " +
            "AND item.available = true")
    List<Item> search(@Param("text") String text);

    List<Item> findByRequestId(Long requestId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.request IN :requests ")
    List<Item> findItemsForItemRequests(@Param("requests") List<ItemRequest> requests);

}
