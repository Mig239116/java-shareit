package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void searchItems_WhenBlankText_ShouldReturnEmptyList() {
        Collection<ItemDto> result = itemService.searchItems(" ");
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_WhenUserDidNotBook_ShouldThrow() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = new User(userId, "test", "test@test.com");
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User(2L, "owner", "owner@test.com"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> itemService.addComment(
                        itemId,
                        userId,
                        new CommentRequestDto("Test comment"))
        );

        assertEquals("User didn't book this item", exception.getMessage());
    }

    private User createTestUser(Long id) {
        return new User(id, "User " + id, "user" + id + "@test.com");
    }

    @Test
    void searchAvailableItem_isValid() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item " + 1L);
        item.setDescription("Description " + 1L);
        item.setAvailable(true);
        item.setOwner(createTestUser(1L));
        item.setBookings(new ArrayList<>());
        item.setComments(new ArrayList<>());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemRepository.search("testItem")).thenReturn(itemList);

        List<ItemDto> actual = itemService.searchItems("testItem").stream().toList();
        assertEquals(itemDto.getId(), actual.get(0).getId());
        assertEquals(itemDto.getName(), actual.get(0).getName());
        assertEquals(itemDto.getDescription(), actual.get(0).getDescription());
    }
}

