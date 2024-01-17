package ru.practicum.exceptions;

public class StatsServiceCreationException extends RuntimeException {
    public StatsServiceCreationException(String message) {
        super(message);
    }
}