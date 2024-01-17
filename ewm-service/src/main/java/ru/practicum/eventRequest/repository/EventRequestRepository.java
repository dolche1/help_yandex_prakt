package ru.practicum.eventRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.eventRequest.model.EventRequestStatus;
import ru.practicum.eventRequest.model.ParticipationRequest;

import java.util.List;
@Repository
public interface EventRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findByRequesterId(Integer requesterId);

    List<ParticipationRequest> findByEventIdAndEventInitiatorId(Integer eventId, Integer initiatorId);

    List<ParticipationRequest> findByIdInAndStatusAndEventId(List<Integer> ids, EventRequestStatus status, Integer eventId);

    Integer countByStatusAndEventId(EventRequestStatus status, Integer eventId);

    Boolean existsByRequesterIdAndEventId(Integer requesterId, Integer eventId);
}