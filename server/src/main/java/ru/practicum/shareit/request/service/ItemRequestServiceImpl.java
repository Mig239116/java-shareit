package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestStorage;
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(NewItemRequestDto requestDto, Long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        ItemRequest request = ItemRequestMapper.newToItemRequest(requestDto);
        request.setRequestor(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestStorage.save(request));
    }

    @Override
    public ResponseItemRequestDto getItemRequest(Long requestId) {
        ItemRequest request = validateNotFound(requestId);
        List<Item> itemList = itemRepository.findByRequestId(requestId);
        ResponseItemRequestDto responseDto = ItemRequestMapper.toResponseDto(request);
        responseDto.setItems(itemList
                .stream()
                .map(ItemMapper::toItemReturnedDto)
                .toList());
        return responseDto;
    }

    @Override
    public Collection<ResponseItemRequestDto> getItemRequests(Long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        Sort sort = Sort.by("created").descending();
        Collection<ItemRequest> requests = itemRequestStorage.findByRequestorId(user.getId(), sort);
        List<Item> allItems = itemRepository.findItemsForItemRequests(requests.stream().toList());
        Map<Long, List<Item>> itemsByRequest = allItems
                .stream()
                .collect(Collectors.groupingBy(b -> b.getRequest().getId()));
        return requests.stream().map(
                request -> {
                    ResponseItemRequestDto requestDto = ItemRequestMapper.toResponseDto(request);
                    List<Item> requestedItems = itemsByRequest.getOrDefault(request.getId(), Collections.emptyList());
                    requestDto.setItems(requestedItems
                            .stream()
                            .map(ItemMapper::toItemReturnedDto)
                            .toList());
                    return requestDto;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public Collection<ResponseItemRequestDto> getAllItemRequests(Long userId) {
        return itemRequestStorage.findAllByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private ItemRequest validateNotFound(Long id) {
        return itemRequestStorage.findById(id).orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("Request " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }


}
