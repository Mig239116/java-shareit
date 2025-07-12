package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemReturnedDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ResponseItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemReturnedDto> items;
}
