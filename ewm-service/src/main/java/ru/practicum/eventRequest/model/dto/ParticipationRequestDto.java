package ru.practicum.eventRequest.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.eventRequest.model.EventRequestStatus;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class ParticipationRequestDto {
    private final Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime created;
    private final Integer event;
    private final Integer requester;
    private final EventRequestStatus status;
}