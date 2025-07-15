package ru.practicum.shareit.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.BadRequestException;

@RestControllerAdvice
public class GlobalExceptionHandlers {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleBadRequestException(BadRequestException e) {
        return new Error("Bad request", e.getMessage());
    }
}
