package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE " +
            "r.size >= :size AND " +
            "NOT EXISTS (SELECT s FROM ScheduleItem s WHERE " +
            "s.room.id = r.id AND " +
            "(s.startTime < :endTime AND s.endTime > :startTime))")
    List<Room> findAvailableRooms(@Param("startTime") LocalDateTime start,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("size") int size);
}