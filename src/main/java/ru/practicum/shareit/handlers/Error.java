package ru.practicum.shareit.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Error {
    private final String error;
    private final String description;
}
