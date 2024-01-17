package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.dto.EventMapper;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.eventRequest.model.EventRequestStatus;
import ru.practicum.eventRequest.repository.EventRequestRepository;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.IncorrectDateException;
import ru.practicum.exceptions.IncorrectOperationException;
import ru.practicum.exceptions.StatsServiceCreationException;
import ru.practicum.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.SortValue;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;
import ru.practicum.statsDto.ViewStatRequestDto;
import ru.practicum.statsDto.ViewStatResponseDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRequestRepository requestStorage;
    private final StatsClient statsClient;

    @Override
    public EventFullDto getEventById(Integer eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException("Событие с id=" + eventId + " не найдено"));
        saveHit(request);
        return EventMapper.eventToFullDto(event, getConfirmed(event), getViews(event));
    }

    @Override
    public List<EventShortDto> getEvents(
            String text,
            List<Integer> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            SortValue sort,
            Integer from,
            Integer size,
            HttpServletRequest request
    ) {
        if (rangeStart == null && rangeEnd == null)
            rangeStart = LocalDateTime.now();
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd))
            throw new IncorrectDateException("Начало диапазона не может быть после его окончания");
        Comparator<EventShortDto> comparator;
        if (sort.equals(SortValue.EVENT_DATE))
            comparator = Comparator.comparing(EventShortDto::getEventDate);
        else comparator = Comparator.comparing(EventShortDto::getViews).reversed();
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        saveHit(request);
        return eventRepository.findEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable).stream()
                .map(event -> EventMapper.eventToShortDto(event, getConfirmed(event), getViews(event)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Integer userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new IncorrectDateException(
                    "Время начала события не может быть раньше, чем через два часа от текущего момента");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));
        int catId = newEventDto.getCategory();
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория с id=" + catId + " не найдена"));
        Event eventFromDto = EventMapper.eventFromNewEventDto(newEventDto, user, category);
        Event event = eventRepository.save(eventFromDto);
        return EventMapper.eventToFullDto(event, 0, 0L);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByInitiator(
            Integer initiatorId,
            Integer eventId,
            UpdateEventUserRequest updateRequest
    ) {
        if (!userRepository.existsById(initiatorId))
            throw new EntityNotFoundException("Пользователь с id=" + initiatorId + " не найден");
        Event event = eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с id=" + eventId + ", созданное пользователем с id=" + initiatorId + ", не найдено"));
        if (event.getState().equals(EventState.PUBLISHED))
            throw new IncorrectOperationException("Изменение опубликованного события невозможно");
        updateEvent(event,
                updateRequest.getAnnotation(),
                updateRequest.getCategory(),
                updateRequest.getDescription(),
                updateRequest.getLocation(),
                updateRequest.getPaid(),
                updateRequest.getParticipantLimit(),
                updateRequest.getRequestModeration(),
                updateRequest.getTitle()
        );
        LocalDateTime eventDate = updateRequest.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2)))
                throw new IncorrectDateException(
                        "Время начала события не может быть раньше, чем через два часа от текущего момента"
                );
            event.setEventDate(eventDate);
        }
        StateAction state = updateRequest.getStateAction();
        if (state != null) {
            switch (state) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                default:
                    throw new IncorrectDateException(
                            "Данная операция недоступна для пользователя"
                    );
            }
        }
        return EventMapper.eventToFullDto(eventRepository.save(event), 0, 0L);
    }

    @Override
    public List<EventShortDto> getEventsByInitiatorId(Integer initiatorId, Integer from, Integer size) {
        if (!userRepository.existsById(initiatorId))
            throw new EntityNotFoundException("Пользователь с id=" + initiatorId + " не найден");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate"));
        List<EventShortDto> list = new ArrayList<>();
        List<Event> byInitiatorId = eventRepository.findByInitiatorId(initiatorId, pageable);
        for (Event event : byInitiatorId) {
            int confirmed = getConfirmed(event);
            long views = getViews(event);
            EventShortDto eventShortDto = EventMapper.eventToShortDto(event, confirmed, views);
            list.add(eventShortDto);
        }
        return list;
    }

    @Override
    public EventFullDto getEventByIdAndInitiatorId(Integer initiatorId, Integer eventId) {
        if (!userRepository.existsById(initiatorId))
            throw new EntityNotFoundException("Пользователь с id=" + initiatorId + " не найден");
        Event event = eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с id=" + eventId + ", созданное пользователем с id=" + initiatorId + ", не найдено"));
        return EventMapper.eventToFullDto(event, getConfirmed(event), getViews(event));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(
            List<Integer> users,
            List<EventState> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    ) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate"));
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd))
            throw new IncorrectDateException("Время начала события не может быть раньше, чем через два часа от текущего момента");
        List<Event> list = eventRepository.findEventsAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        List<EventFullDto> list2 = new ArrayList<>();
        for (Event event : list) {
            EventFullDto event2  =EventMapper.eventToFullDto(event, getConfirmed(event), getViews(event));
            list2.add(event2);
        }
        return list2;


    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Integer eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с id=" + eventId + " не найдено"));
        EventState eventState = event.getState();
        if (!eventState.equals(EventState.PENDING))
            throw new IncorrectOperationException("Изменение события с id=" + eventId + " невозможно");
        updateEvent(
                event,
                updateRequest.getAnnotation(),
                updateRequest.getCategory(),
                updateRequest.getDescription(),
                updateRequest.getLocation(),
                updateRequest.getPaid(),
                updateRequest.getParticipantLimit(),
                updateRequest.getRequestModeration(),
                updateRequest.getTitle()
        );
        LocalDateTime eventDate = updateRequest.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1)))
                throw new IncorrectDateException(
                        "Время начала события не может быть раньше, чем через час от текущего момента"
                );
            event.setEventDate(eventDate);
        }
        StateAction state = updateRequest.getStateAction();
        if (state != null) {
            switch (state) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                default:
                    throw new IncorrectOperationException("Данная операция недоступна для администратора");
            }
        }
        return EventMapper.eventToFullDto(eventRepository.save(event), 0, 0L);
    }
    @Transactional
    public void updateEvent(
            Event event,
            String annotation,
            Integer catId,
            String description,
            Location location,
            Boolean paid,
            Integer participantLimit,
            Boolean requestModeration,
            String title
    ) {
        if (annotation != null) event.setAnnotation(annotation);
        if (catId != null) {
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new EntityNotFoundException("Категория с id=" + catId + " не найдена"));
            event.setCategory(category);
        }
        if (description != null) event.setDescription(description);
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) event.setParticipantLimit(participantLimit);
        if (requestModeration != null) event.setRequestModeration(requestModeration);
        if (title != null) event.setTitle(title);
    }
    @Transactional
    public void saveHit(HttpServletRequest request) {
        ViewStatRequestDto viewStatRequestDto = ViewStatRequestDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        ResponseEntity<Object> response = statsClient.addEndpointHit(viewStatRequestDto);
        if (!response.getStatusCode().is2xxSuccessful())
            throw new StatsServiceCreationException("Запись в сервис статистики не сохранилась");
    }
    @Transactional
    public long getViews(Event event) {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<Object> response = statsClient.getStats( event.getPublishedOn(), LocalDateTime.now(), Collections.singletonList(String.format("/events/%s", event.getId())), false);
        List<ViewStatResponseDto> viewStatsList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        return (long) viewStatsList.size();


        /*
        if (!event.getState().equals(EventState.PUBLISHED)) return 0;
        ResponseEntity<Object> response = statsClient.getStats(
                event.getPublishedOn(),
                LocalDateTime.now(),
                List.of(String.format("/events/%s", event.getId())),
                true
        );
        ObjectMapper mapper = new ObjectMapper();
        List<ViewStatResponseDto> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        return (statsDto.isEmpty()) ? 0 : statsDto.get(0).getHits();*/
    }
    @Transactional
    public int getConfirmed(Event event) {
        return (event.getState().equals(EventState.PUBLISHED)) ?
                requestStorage.countByStatusAndEventId(EventRequestStatus.CONFIRMED, event.getId()) : 0;
    }
}
