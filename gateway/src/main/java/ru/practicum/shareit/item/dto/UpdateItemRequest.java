package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UpdateItemRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

}
