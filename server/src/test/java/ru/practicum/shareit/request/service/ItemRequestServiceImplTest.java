package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestStorage;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void addItemRequest_shouldSaveRequest() {
        NewItemRequestDto requestDto = new NewItemRequestDto("Need a drill");
        User user = new User(1L, "John", "john@example.com");
        ItemRequest expectedRequest = ItemRequestMapper.newToItemRequest(requestDto);
        expectedRequest.setRequestor(user);

        when(userService.getUser(anyLong())).thenReturn(UserMapper.toUserDto(user));
        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(expectedRequest);

        ItemRequestDto result = itemRequestService.addItemRequest(requestDto, 1L);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        assertEquals(user, result.getRequestor());
        verify(itemRequestStorage, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequest_shouldReturnRequestWithItems() {
        User user = new User(1L, "John", "john@example.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
        Item item = new Item(1L, "Drill", "Powerful drill", true, user, null, null, null);

        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        ResponseItemRequestDto result = itemRequestService.getItemRequest(1L);


        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Drill", result.getItems().get(0).getName());
    }

    @Test
    void getItemRequest_whenRequestNotFound_shouldThrowException() {

        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(1L));
    }

    @Test
    void getItemRequests_shouldReturnUserRequestsWithItems() {
        User user = new User(1L, "John", "john@example.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
        Item item = new Item(1L, "Drill", "Powerful drill", true, user, request, null, null);

        when(userService.getUser(anyLong())).thenReturn(UserMapper.toUserDto(user));
        when(itemRequestStorage.findByRequestorId(anyLong(), any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findItemsForItemRequests(anyList())).thenReturn(List.of(item));

        Collection<ResponseItemRequestDto> result = itemRequestService.getItemRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need a drill", result.iterator().next().getDescription());
        assertEquals(1, result.iterator().next().getItems().size());
    }

    @Test
    void getAllItemRequests_shouldReturnOtherUsersRequests() {
        User user1 = new User(1L, "John", "john@example.com");
        User user2 = new User(2L, "Alice", "alice@example.com");
        ItemRequest request = new ItemRequest(1L, "Need a drill", user2, LocalDateTime.now());

        when(itemRequestStorage.findAllByRequestorIdNotOrderByCreatedDesc(anyLong())).thenReturn(List.of(request));

        Collection<ResponseItemRequestDto> result = itemRequestService.getAllItemRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need a drill", result.iterator().next().getDescription());
    }

    @Test
    void getAllItemRequests_whenNoRequests_shouldReturnEmptyList() {
        when(itemRequestStorage.findAllByRequestorIdNotOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        Collection<ResponseItemRequestDto> result = itemRequestService.getAllItemRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllItemRequests_ShouldHandlePagination() {
        when(itemRequestStorage.findAllByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(new ItemRequest(), new ItemRequest()));

        Collection<ResponseItemRequestDto> result = itemRequestService.getAllItemRequests(1L);
        assertEquals(2, result.size());
    }
}