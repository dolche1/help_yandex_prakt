package ru.practicum.eventRequest.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.eventRequest.model.EventRequestStatus;
import ru.practicum.eventRequest.model.ParticipationRequest;
import ru.practicum.eventRequest.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.eventRequest.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.eventRequest.model.dto.ParticipationRequestDto;
import ru.practicum.eventRequest.model.dto.ParticipationRequestMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.eventRequest.repository.EventRequestRepository;
import ru.practicum.exceptions.EntityAlreadyExists;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.IncorrectOperationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestRepository requestStorage;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByEventInitiator(
            Integer initiatorId,
            Integer eventId
    ) {
        if (!userRepository.existsById(initiatorId))
            throw new EntityNotFoundException("Пользователь с id=" + initiatorId + " не найден");
        if (!eventRepository.existsById(eventId))
            throw new EntityNotFoundException("Событие с id=" + eventId + " не найдено");
        return requestStorage.findByEventIdAndEventInitiatorId(eventId, initiatorId).stream()
                .map(ParticipationRequestMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequests(
            Integer initiatorId,
            Integer eventId,
            EventRequestStatusUpdateRequest updateRequest
    ) {
        if (!userRepository.existsById(initiatorId))
            throw new EntityNotFoundException("Пользователь с id=" + initiatorId + " не найден");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с id=" + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(initiatorId))
            throw new IncorrectOperationException(
                    "Пользователь с id=" + initiatorId +
                            " не является инициатором события с id=" + eventId
            );
        if (!event.isRequestModeration()) throw new IncorrectOperationException(
                "Для события с id=" + eventId + " одобрение заявок не требуется"
        );
        int limit = event.getParticipantLimit();
        int vacant = limit - requestStorage.countByStatusAndEventId(EventRequestStatus.CONFIRMED, eventId);
        if (limit > 0 && vacant == 0) throw new IncorrectOperationException(
                "Достигнут лимит одобренных заявок для события с id=" + eventId
        );
        List<Integer> ids = updateRequest.getRequestIds();
        List<ParticipationRequest> requests = requestStorage.findByIdInAndStatusAndEventId(
                ids,
                EventRequestStatus.PENDING,
                eventId
        );
        EventRequestStatus status = updateRequest.getStatus();
        if (status.equals(EventRequestStatus.REJECTED)) {
            for (ParticipationRequest request : requests)
                request.setStatus(EventRequestStatus.REJECTED);
        } else if (status.equals(EventRequestStatus.CONFIRMED)) {
            int size = requests.size();
            for (int i = 0; (i < vacant && i < size); i++)
                requests.get(i).setStatus(EventRequestStatus.CONFIRMED);
            if (size > vacant)
                for (int i = vacant; i < size; i++)
                    requests.get(i).setStatus(EventRequestStatus.REJECTED);
        } else throw new IncorrectOperationException(
                "Некорректный статус в заявке на изменение статусов запросов на участие"
        );
        requestStorage.saveAll(requests);
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            if (request.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                confirmedRequests.add(ParticipationRequestMapper.participationRequestToDto(request));
            } else {
                rejectedRequests.add(ParticipationRequestMapper.participationRequestToDto(request));
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByRequester(Integer requesterId) {
        if (!userRepository.existsById(requesterId))
            throw new EntityNotFoundException("Пользователь с id=" + requesterId + " не найден");
        return requestStorage.findByRequesterId(requesterId).stream()
                .map(ParticipationRequestMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequest(Integer requesterId, Integer eventId) {
        if (requestStorage.existsByRequesterIdAndEventId(requesterId, eventId))
            throw new EntityAlreadyExists(
                    "Запрос на участие пользователя с id=" + requesterId +
                            " в событии с id=" + eventId + " уже существует"
            );
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с id=" + eventId + " не найдено"));
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + requesterId + " не найден"));
        if (event.getInitiator().getId().equals(requesterId))
            throw new IncorrectOperationException(
                    "Пользователь с id=" + requesterId +
                            " отправил запрос на участие в собственном событии с id=" + eventId
            );
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new IncorrectOperationException(
                    "Пользователь с id=" + requesterId +
                            " отправил запрос на участие в неопубликованном событии с id=" + eventId
            );
        int limit = event.getParticipantLimit();
        boolean hasReachedLimit =
                (limit > 0) &&
                        requestStorage
                                .countByStatusAndEventId(EventRequestStatus.CONFIRMED, eventId)
                                .equals(limit);
        if (hasReachedLimit)
            throw new IncorrectOperationException(
                    "Пользователь с id=" + requesterId +
                            " отправил запрос на участие в событии с id=" + eventId + " с достигнутым лимитом запросов"
            );
        EventRequestStatus status = (event.isRequestModeration() && limit > 0)
                ? EventRequestStatus.PENDING : EventRequestStatus.CONFIRMED;
        ParticipationRequest request = requestStorage.save(
                new ParticipationRequest(
                        null,
                        LocalDateTime.now(),
                        event,
                        user,
                        status
                )
        );
        return ParticipationRequestMapper.participationRequestToDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Integer userId, Integer requestId) {
        ParticipationRequest request = requestStorage.findById(requestId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Запрос на участие с id=" + requestId + " не найден"
                        )
                );
        if (!request.getRequester().getId().equals(userId))
            throw new IncorrectOperationException(
                    "Запрос на участие с id=" + requestId + " не принадлежит пользователю с id=" + userId
            );
        request.setStatus(EventRequestStatus.CANCELED);
        return ParticipationRequestMapper.participationRequestToDto(requestStorage.save(request));
    }
}