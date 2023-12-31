package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.statsDto.ViewStatRequestDto;
import ru.practicum.statsDto.ViewStatResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StatsMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EndpointHit mapToEndpointHit(ViewStatRequestDto viewStatRequestDto) {
        return EndpointHit.builder()
                .app(viewStatRequestDto.getApp())
                .uri(viewStatRequestDto.getUri())
                .ip(viewStatRequestDto.getIp())
                .timestamp(LocalDateTime.parse(viewStatRequestDto.getTimestamp(), formatter))
                .build();
    }

    public ViewStatResponseDto mapToViewStatResponseDto(ViewStats viewStats) {
        return ViewStatResponseDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

}