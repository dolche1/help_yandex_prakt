package ru.practicum.event.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Location {
    private final double lat;
    private final double lon;
}
