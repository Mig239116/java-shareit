package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;


    private String name;


    private String description;


    private Boolean available;
    private User owner;
    private ItemRequest request;
    private List<CommentDto> comments;
    private LocalDate lastBooking;
    private LocalDate nextBooking;
}
