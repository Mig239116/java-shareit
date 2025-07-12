package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findByRequestorId_shouldReturnRequestsSortedByCreatedDesc() {
        User user = new User(null, "John", "john@example.com");
        em.persist(user);

        ItemRequest request1 = new ItemRequest(null, "Need drill", user, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = new ItemRequest(null, "Need hammer", user, LocalDateTime.now());
        em.persist(request1);
        em.persist(request2);
        em.flush();

        List<ItemRequest> result = itemRequestRepository.findByRequestorId(
                user.getId(),
                Sort.by(Sort.Direction.DESC, "created"));

        assertEquals(2, result.size());
        assertEquals("Need hammer", result.get(0).getDescription());
        assertEquals("Need drill", result.get(1).getDescription());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_shouldReturnOtherUsersRequests() {
        User user1 = new User(null, "John", "john@example.com");
        User user2 = new User(null, "Alice", "alice@example.com");
        em.persist(user1);
        em.persist(user2);

        ItemRequest request1 = new ItemRequest(null, "Need drill", user1, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = new ItemRequest(null, "Need hammer", user2, LocalDateTime.now());
        em.persist(request1);
        em.persist(request2);
        em.flush();

        List<ItemRequest> result = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user1.getId());

        assertEquals(1, result.size());
        assertEquals("Need hammer", result.get(0).getDescription());
        assertEquals(user2.getId(), result.get(0).getRequestor().getId());
    }
}
