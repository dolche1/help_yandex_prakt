package ru.practicum.user.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class UserDto {
    private final Integer id;
    private final String email;
    private final String name;
}