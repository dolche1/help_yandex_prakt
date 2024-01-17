package ru.practicum.event.service;


import ru.practicum.event.model.EventState;
import ru.practicum.event.model.SortValue;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Integer userId, NewEventDto newEventDto);

    EventFullDto updateEventByAdmin(Integer eventId, UpdateEventAdminRequest updateRequest);
    EventFullDto updateEventByInitiator(Integer initiatorId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);
    EventFullDto getEventById(Integer id, HttpServletRequest request);

    List<EventShortDto> getEvents(String text,
                                  List<Integer> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  SortValue sort,
                                  Integer from,
                                  Integer size,
                                  HttpServletRequest request
    );
    List<EventShortDto> getEventsByInitiatorId(Integer initiatorId, Integer from, Integer size);

    EventFullDto getEventByIdAndInitiatorId(Integer initiatorId, Integer eventId);

    List<EventFullDto> getEventsByAdmin(
            List<Integer> users,
            List<EventState> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    );
}
