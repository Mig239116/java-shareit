package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody NewItemRequestDto requestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("POST/requests - adding new request {} by user {}",
                requestDto.getDescription(),
                userId);
        return requestClient.addRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET/requests: all requests of the user {} returned", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET/requests: all requests returned");
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable Long requestId) {
        log.debug("GET/requests/id: returning request {}", requestId);
        return requestClient.getRequest(requestId);
    }
}
