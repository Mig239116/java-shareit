package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

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
        owner = new User(null, "Owner", "owner@example.com");
        requestor = new User(null, "Requestor", "requestor@example.com");
        em.persist(owner);
        em.persist(requestor);

        request = new ItemRequest(null, "Need item", requestor, null);
        em.persist(request);

        item1 = new Item(null, "Item1", "Description1", true, owner, null, null, null);
        item2 = new Item(null, "Item2", "Description2", true, owner, request, null, null);
        em.persist(item1);
        em.persist(item2);
        em.flush();
    }

    @Test
    void findByOwnerId_shouldReturnItemsForOwner() {
        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Item1")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Item2")));
    }

    @Test
    void search_shouldFindAvailableItemsByText() {
        List<Item> items = itemRepository.search("item1");

        assertEquals(1, items.size());
        assertEquals("Item1", items.get(0).getName());
    }

    @Test
    void search_shouldReturnEmptyListForBlankText() {
        List<Item> items = itemRepository.search(" ");
        assertTrue(items.isEmpty());
    }

    @Test
    void findByRequestId_shouldReturnItemsForRequest() {
        List<Item> items = itemRepository.findByRequestId(request.getId());

        assertEquals(1, items.size());
        assertEquals("Item2", items.get(0).getName());
    }

    @Test
    void findItemsForItemRequests_shouldReturnItemsForRequests() {
        List<Item> items = itemRepository.findItemsForItemRequests(List.of(request));

        assertEquals(1, items.size());
        assertEquals("Item2", items.get(0).getName());
    }
}
