package ru.practicum.shareit.request.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(NewItemRequestDto requestDto, Long userId);

    Collection<ResponseItemRequestDto> getItemRequests(Long userId);

    Collection<ResponseItemRequestDto> getAllItemRequests(Long userId);

    ResponseItemRequestDto getItemRequest(Long requestId);
}
