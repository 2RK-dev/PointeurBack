package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_item_id")
    private Long id;

    private LocalDateTime startTime;  // Format ISO 8601: 2025-07-11T08:30:00
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime start) {
        this.startTime = start;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public TeachingUnit getTeachingUnit() {
        return teachingUnit;
    }

    public void setTeachingUnit(TeachingUnit teachingUnit) {
        this.teachingUnit = teachingUnit;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups != null ? groups : new ArrayList<>();
    }

    // Bidirectional Relationship Helpers
    public void addGroup(Group group) {
        if (!this.groups.contains(group)) {
            this.groups.add(group);
            group.getSchedules().add(this);
        }
    }

    public void removeGroup(Group group) {
        if (this.groups.remove(group)) {
            group.getSchedules().remove(this);
        }
    }

    // Room relationship synchronization
    public void addRoom(Room newRoom) {
        // Prevent infinite loop
        if (this.room == newRoom) {
            return;
        }

        // Clear previous room's reference
        Room oldRoom = this.room;
        if (oldRoom != null) {
            oldRoom.getSchedules().remove(this);
        }

        // Set new room
        this.room = newRoom;

        // Add to new room's schedule list
        if (newRoom != null && !newRoom.getSchedules().contains(this)) {
            newRoom.getSchedules().add(this);
        }
    }
}

