package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {
    @Query(value = "SELECT app , uri , COUNT( ip) as hits FROM stat"
            + " WHERE uri IN (:uris) AND timestamp BETWEEN :start and :end GROUP BY uri,app ORDER BY hits DESC", nativeQuery = true)
    List<ViewStats> getStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query(value = "SELECT app , uri , COUNT( ip) as hits FROM stat"
            + " WHERE timestamp BETWEEN :start and :end GROUP BY uri,app ORDER BY hits DESC", nativeQuery = true)
    List<ViewStats> getStatsWithoutUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT DISTINCT app , uri , COUNT(DISTINCT ip) as hits FROM stat"
            + " WHERE uri IN (:uris) AND timestamp BETWEEN :start and :end GROUP BY uri,app ORDER BY hits DESC", nativeQuery = true)
    List<ViewStats> getStatsUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query(value = "SELECT DISTINCT app , uri , COUNT(DISTINCT ip) as hits FROM stat"
            + " WHERE timestamp BETWEEN :start and :end GROUP BY uri,app ORDER BY hits DESC", nativeQuery = true)
    List<ViewStats> getStatsUniqueWithoutUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
