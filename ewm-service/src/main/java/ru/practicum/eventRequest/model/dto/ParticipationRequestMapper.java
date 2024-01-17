package ru.practicum.eventRequest.model.dto;

import ru.practicum.eventRequest.model.ParticipationRequest;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto participationRequestToDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .build();
    }
}
