package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorId(Long requestorId, Sort sort);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id <> :requestorId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}
