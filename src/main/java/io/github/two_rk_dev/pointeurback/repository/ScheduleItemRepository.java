package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {

    List<ScheduleItem> findByGroupsId(Long groupId);

    List<ScheduleItem> findByTeacherId(Long teacherId);

    List<ScheduleItem> findByTeachingUnitId(Long teachingUnitId);

    List<ScheduleItem> findByRoomId(Long roomId);

    @Query("SELECT s FROM ScheduleItem s WHERE EXISTS (SELECT 1 FROM s.groups g WHERE g.level.id = :levelId)")
    List<ScheduleItem> findByLevelId(@Param("levelId") Long levelId);

    @Query("SELECT DISTINCT si FROM ScheduleItem si " +
            "LEFT JOIN si.groups g " +
            "WHERE ((si.room.id = :roomId OR si.teacher.id = :teacherId OR g.id IN :groupIds) " +
            "AND (si.startTime < :end AND si.endTime > :startTime))")
    List<ScheduleItem> findConflictingSchedule(
            @Param("startTime") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("roomId") Long roomId,
            @Param("teacherId") Long teacherId,
            @Param("groupIds") List<Long> groupIds);

    List<ScheduleItem> findByStartBetween(LocalDateTime start, LocalDateTime endTime);
}

