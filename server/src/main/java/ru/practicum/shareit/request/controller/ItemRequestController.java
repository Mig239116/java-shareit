package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(@Qualifier("itemRequestServiceImpl") ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto addItemRequest(@RequestBody NewItemRequestDto requestDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("POST/requests - adding new request {} by user {}",
                requestDto.getDescription(),
                userId);
        ItemRequestDto responseRequestDto  = itemRequestService.addItemRequest(requestDto, userId);
        log.debug("POST/requests: the process was completed successfully. A new request {} with id {} has been created by user {}",
                responseRequestDto.getDescription(),
                responseRequestDto.getId(),
                responseRequestDto.getRequestor()
        );
        return responseRequestDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ResponseItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET/requests: all requests of the user {} returned", userId);
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ResponseItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET/requests: all requests returned");
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseItemRequestDto getItemRequest(@PathVariable Long requestId) {
        log.debug("GET/requests/id: returning request {}", requestId);
        return itemRequestService.getItemRequest(requestId);
    }

}
