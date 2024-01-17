package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.user.model.dto.UserShortDto;


import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class EventShortDto {
    private final Integer id;
    private final UserShortDto initiator;
    private final String annotation;
    private final CategoryDto category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
    private final Boolean paid;
    private final String title;
    private final Integer confirmedRequests;
    private final Long views;
}