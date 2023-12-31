package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;
import ru.practicum.statsDto.ViewStatRequestDto;
import ru.practicum.statsDto.ViewStatResponseDto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    public static final String ITEM_NOT_FOUND_MESSAGE = "Item with id %d not found";
    private static final String WRONG_OWNER_MESSAGE = "You are not an owner ot this item!";

    private static final String BOOKING_FOR_COMMENT_NOT_FOUND_EXCEPTION_MESSAGE = " You have not booked item %d to comment";

    private final StatsMapper mapper;
    private final StatsRepository statsRepository;


    @Override
    public void addEndpointHit(ViewStatRequestDto viewStatRequestDto) {
        statsRepository.save(mapper.mapToEndpointHit(viewStatRequestDto));
    }


    @Override
    public List<ViewStatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> result;
        if (unique) {
            if (uris == null) {
                result = statsRepository.getStatsUniqueWithoutUris(start, end);
            } else {
                result = statsRepository.getStatsUnique(start, end, uris);
            }
        } else {
            if (uris == null) {
                result = statsRepository.getStatsWithoutUris(start, end);
            } else {
                result = statsRepository.getStats(start, end, uris);
            }

        }
        return result.stream().map(mapper::mapToViewStatResponseDto).collect(Collectors.toList());
    }
}