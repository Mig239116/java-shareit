package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User requestor;
    private ItemRequest request;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@email.com");
        requestor = new User(null, "Requestor", "requestor@email.com");
        em.persist(owner);
        em.persist(requestor);

        request = new ItemRequest(null, "Need item", requestor, LocalDateTime.now());
        em.persist(request);

        item1 = new Item(null, "Дрель", "Аккумуляторная дрель", true, owner, null, null, null);
        item2 = new Item(null, "Отвертка", "Крестовая отвертка", true, owner, request, null, null);
        em.persist(item1);
        em.persist(item2);
    }

    @Test
    void findByOwnerId_shouldReturnItemsForOwner() {
        List<Item> result = itemRepository.findByOwnerId(owner.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getName().equals("Дрель")));
        assertTrue(result.stream().anyMatch(i -> i.getName().equals("Отвертка")));
    }

    @Test
    void addItem_shouldSaveItem() {
        Item newItem = new Item(null, "Отвертка", "Крестовая отвертка", true, owner, null, new ArrayList<>(), new ArrayList<>());

        Item savedItem = itemRepository.save(newItem);

        assertNotNull(savedItem.getId());
        assertEquals(newItem.getName(), savedItem.getName());
        assertEquals(owner.getId(), savedItem.getOwner().getId());
    }

    @Test
    void search_shouldFindAvailableItemsByText() {
        List<Item> result = itemRepository.search("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.get(0).getName());
    }


    @Test
    void findByRequestId_shouldReturnItemsForRequest() {
        List<Item> result = itemRepository.findByRequestId(request.getId());

        assertEquals(1, result.size());
        assertEquals("Отвертка", result.get(0).getName());
    }

    @Test
    void findItemsForItemRequests_shouldReturnItemsForRequests() {
        List<ItemRequest> requests = List.of(request);
        List<Item> result = itemRepository.findItemsForItemRequests(requests);

        assertEquals(1, result.size());
        assertEquals("Отвертка", result.get(0).getName());
    }


    @Test
    void getItems_shouldReturnEmptyListForWrongUser() {
        List<Item> items = itemRepository.findByOwnerId(999L);

        assertTrue(items.isEmpty());
    }

    @Test
    void searchItems_shouldNotReturnUnavailable() {
        item1.setAvailable(false);
        itemRepository.save(item1);

        List<Item> result = itemRepository.search("дрель");

        assertTrue(result.isEmpty());
    }
}
