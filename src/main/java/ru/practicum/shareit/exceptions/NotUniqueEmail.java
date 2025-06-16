package ru.practicum.shareit.exceptions;

public class NotUniqueEmail extends RuntimeException {
    public NotUniqueEmail(String message) {
        super(message);
    }
}
