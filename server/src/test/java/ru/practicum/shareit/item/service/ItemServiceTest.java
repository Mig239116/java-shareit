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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
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
}

