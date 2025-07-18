package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_item_id")
    private Long id;

    private LocalDateTime start;  // Format ISO 8601: 2025-07-11T08:30:00
    private LocalDateTime endTime;    // Format ISO 8601: 2025-07-11T10:30:00

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "teaching_unit_id")
    private TeachingUnit teachingUnit;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToMany
    @JoinTable(
            name = "schedule_item_groups",
            joinColumns = @JoinColumn(name = "schedule_item_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<Group> groups;

    // Méthodes utilitaires pour gérer la relation bidirectionnelle
    public void addGroup(Group group) {
        this.groups.add(group);
        group.getSchedules().add(this);
    }

    public void removeGroup(Group group) {
        this.groups.remove(group);
        group.getSchedules().remove(this);
        group.getSchedules().remove(this);
    }

//    @OneToMany(mappedBy = "schedule")
//    private List<Attendance> attendances;

    // Getters et setters
}
