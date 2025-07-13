package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    // Recherche par groupe
    List<ScheduleItem> findByGroupId(Long groupId);

    // Recherche par enseignant
    List<ScheduleItem> findByTeacherId(Long teacherId);

    // Recherche par unité d'enseignement
    List<ScheduleItem> findByTeachingUnitId(Long teachingUnitId);

    // Recherche par salle
    List<ScheduleItem> findByRoomId(Long roomId);

    // Recherche par période
    List<ScheduleItem> findByStartBetween(LocalDateTime start, LocalDateTime end);
}

