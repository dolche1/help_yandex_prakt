package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Event;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Optional<Event> findByIdAndState(Integer eventId, EventState state);

    @Query("SELECT e FROM Event AS e WHERE ((lower(e.annotation) LIKE lower(concat('%', :text, '%'))) " +
            "OR (lower(e.description) LIKE lower(concat('%', :text, '%'))) OR (:text = null)) " +
            "AND ((e.category.id IN :categories) OR (:categories = null)) " +
            "AND ((e.paid = :paid) OR (:paid = null)) " +
            "AND ((e.eventDate >= :rangeStart) OR (cast(:rangeStart as timestamp) = null)) " +
            "AND ((e.eventDate <= :rangeEnd) OR (cast(:rangeEnd as timestamp) = null)) " +
            "AND ((:onlyAvailable = false) OR ((:onlyAvailable = true) AND ((e.participantLimit = 0) " +
            "OR ((e.participantLimit - (SELECT COUNT(r) FROM ParticipationRequest r " +
            "WHERE (r.event.id = e.id) AND (r.status = 'CONFIRMED'))) < 0)))) " +
            "AND (e.state = 'PUBLISHED')")
    List<Event> findEventsPublic(
            String text,
            List<Integer> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            Pageable pageable);

    List<Event> findByInitiatorId(Integer initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Integer eventId, Integer initiatorId);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE ((e.initiator.id IN :users) OR (:users = null)) " +
            "AND ((e.state IN :states) OR (:states = null)) " +
            "AND ((e.category.id IN :categories) OR (:categories = null)) " +
            "AND ((e.eventDate >= :rangeStart) OR (cast(:rangeStart as timestamp) = null)) " +
            "AND ((e.eventDate <= :rangeEnd) OR (cast(:rangeEnd as timestamp) = null))")
    List<Event> findEventsAdmin(
            List<Integer> users,
            List<EventState> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);

    Integer countByCategoryId(Integer catId);
}
