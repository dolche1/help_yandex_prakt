package ru.practicum.compilation.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.dto.EventShortDto;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class CompilationDto {
    private final Integer id;
    private final Boolean pinned;
    private final String title;
    private final List<EventShortDto> events;
}
