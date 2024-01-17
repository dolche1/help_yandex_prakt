package ru.practicum.eventRequest.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.eventRequest.model.EventRequestStatus;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
        private final List<Integer> requestIds;
        private final EventRequestStatus status;
}
