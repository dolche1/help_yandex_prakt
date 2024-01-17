package ru.practicum.exceptions;

public class IncorrectOperationException extends RuntimeException {
    public IncorrectOperationException(String message) {
        super(message);
    }
}