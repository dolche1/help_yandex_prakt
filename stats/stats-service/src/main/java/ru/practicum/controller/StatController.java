package ru.practicum.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatService;
import ru.practicum.statsDto.ViewStatRequestDto;
import ru.practicum.statsDto.ViewStatResponseDto;


import java.time.LocalDateTime;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/")
@RequiredArgsConstructor
public class StatController {


    private static final String LOGGER_GET_STATS_MESSAGE = "Returning stats by start {} , end {} for uries {} and unique is {}";

    private static final String LOGGER_ADD_HIT_MESSAGE = "Adding hit with ip {} to uri {}";

    private final StatService statService;


    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody @Validated ViewStatRequestDto viewStatRequestDto) {
        log.info(LOGGER_ADD_HIT_MESSAGE, viewStatRequestDto.getIp(), viewStatRequestDto.getUri());
        statService.addEndpointHit(viewStatRequestDto);
    }


    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatResponseDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime start,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime end,
                                              @RequestParam(required = false) List<String> uris,
                                              @RequestParam(defaultValue = "false") boolean unique) {
        log.info(LOGGER_GET_STATS_MESSAGE, start, end, uris, unique);
        return statService.getStats(start, end, uris, unique);
    }


}
