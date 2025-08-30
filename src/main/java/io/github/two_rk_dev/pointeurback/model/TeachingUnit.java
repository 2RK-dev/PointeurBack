package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeachingUnit {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    private String name;

    @Setter
    private String abbreviation;

    @Setter
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @OneToMany(mappedBy = "teachingUnit")
    private List<ScheduleItem> schedules;

    public void setSchedules(List<ScheduleItem> schedules) {
        this.schedules = schedules != null ? schedules : new ArrayList<>();
    }

    // Bidirectional Relationship Management
    public void addScheduleItem(ScheduleItem scheduleItem) {
        if (!schedules.contains(scheduleItem)) {
            schedules.add(scheduleItem);
            scheduleItem.setTeachingUnit(this);
        }
    }

    public void removeScheduleItem(ScheduleItem scheduleItem) {
        if (schedules.remove(scheduleItem)) {
            scheduleItem.setTeachingUnit(null);
        }
    }
}