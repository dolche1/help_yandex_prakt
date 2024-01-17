package ru.practicum.compilation.model.dto;


import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventMapper;

import java.util.Set;
import java.util.stream.Collectors;


public class CompilationMapper {
    public static CompilationDto compilationToDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(event -> EventMapper.eventToShortDto(event, null, null))
                        .collect(Collectors.toList()))
                .build();
    }

    public static Compilation compilationFromNewDto(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .id(null)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .events(events)
                .build();
    }
}
