package ru.practicum.event.model.dto;

import ru.practicum.category.model.Category;
import ru.practicum.category.model.dto.CategoryMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.model.User;
import ru.practicum.user.model.dto.UserMapper;

import java.time.LocalDateTime;

import static ru.practicum.category.model.dto.CategoryMapper.categoryToCategoryDto;
import static ru.practicum.user.model.dto.UserMapper.userToShortDto;


public class EventMapper {
    public static EventShortDto eventToShortDto(Event event, Integer confirmedRequests, Long hits) {
        return EventShortDto.builder()
                .id(event.getId())
                .initiator(userToShortDto(event.getInitiator()))
                .annotation(event.getAnnotation())
                .category(categoryToCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .title(event.getTitle())
                .confirmedRequests(confirmedRequests)
                .views(hits)
                .build();
    }

    public static EventFullDto eventToFullDto(Event event, Integer confirmedRequests, Long hits) {
        return new EventFullDto(
                event.getId(),
                userToShortDto(event.getInitiator()),
                event.getAnnotation(),
                categoryToCategoryDto(event.getCategory()),
                event.getDescription(),
                event.getEventDate(),
                new Location(event.getLat(), event.getLon()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.isRequestModeration(),
                event.getTitle(),
                event.getCreatedOn(),
                event.getState(),
                event.getPublishedOn(),
                confirmedRequests,
                hits
        );
    }

    public static Event eventFromNewEventDto(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .id(null)
                .initiator(initiator)
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .title(newEventDto.getTitle())
                .requestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .publishedOn(null)
                .build();
    }
}
