package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ScheduleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime start;  // Format ISO 8601: 2025-07-11T08:30:00
    private LocalDateTime end;    // Format ISO 8601: 2025-07-11T10:30:00

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "teaching_unit_id")
    private TeachingUnit teachingUnit;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group groups;

//    @OneToMany(mappedBy = "schedule")
//    private List<Attendance> attendances;

    // Getters et setters
}
