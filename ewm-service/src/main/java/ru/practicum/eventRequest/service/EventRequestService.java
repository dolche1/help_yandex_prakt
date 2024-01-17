package ru.practicum.eventRequest.service;

import ru.practicum.eventRequest.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.eventRequest.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.eventRequest.model.dto.ParticipationRequestDto;

import java.util.List;

public interface EventRequestService {
    List<ParticipationRequestDto> getParticipationRequestsByEventInitiator(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateParticipationRequests(
            Integer userId,
            Integer eventId,
            EventRequestStatusUpdateRequest updateRequest
    );

    List<ParticipationRequestDto> getParticipationRequestsByRequester(Integer requesterId);

    ParticipationRequestDto addParticipationRequest(Integer userId, Integer eventId);

    ParticipationRequestDto cancelParticipationRequest(Integer userId, Integer requestId);
}
