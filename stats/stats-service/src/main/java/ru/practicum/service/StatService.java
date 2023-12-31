package ru.practicum.service;


import ru.practicum.statsDto.ViewStatRequestDto;
import ru.practicum.statsDto.ViewStatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addEndpointHit(ViewStatRequestDto viewStatRequestDto);

    List<ViewStatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
