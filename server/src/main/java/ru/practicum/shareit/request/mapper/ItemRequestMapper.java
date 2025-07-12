package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest newToItemRequest(NewItemRequestDto newRequestDto) {
        return new ItemRequest(
                null,
                newRequestDto.getDescription(),
                null,
                LocalDateTime.now()
        );
    }

    public static ResponseItemRequestDto toResponseDto(ItemRequest request) {
        return new ResponseItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated(),
                null
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated()
        );
    }
}
