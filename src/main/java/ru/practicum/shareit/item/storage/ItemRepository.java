package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT item FROM Item item " +
            "WHERE (upper(item.name) LIKE upper(concat('%', :text, '%')) " +
            "OR upper(item.description) LIKE upper(concat('%', :text, '%'))) " +
            "AND item.available = true")
    List<Item> search(@Param("text") String text);
}
