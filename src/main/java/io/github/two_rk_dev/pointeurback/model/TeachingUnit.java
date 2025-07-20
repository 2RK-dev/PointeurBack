package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeachingUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teaching_unit_id")
    private Long id;

    private String name;

    private String abbreviation;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @OneToMany(mappedBy = "teachingUnit")
    private List<ScheduleItem> schedules;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<ScheduleItem> getSchedules() {
        return schedules;
    }

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