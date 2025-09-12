package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleItem {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_item_id")
    private Long id;

    @Setter
    private OffsetDateTime startTime;
    @Setter
    private OffsetDateTime endTime;

    @Setter
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Setter
    @ManyToOne
    @JoinColumn(name = "teaching_unit_id")
    private TeachingUnit teachingUnit;

    @Setter
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


    public void setGroups(List<Group> groups) {
        this.groups = groups != null ? groups : new ArrayList<>();
    }

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

    public void addRoom(Room newRoom) {
        if (this.room == newRoom) {
            return;
        }

        Room oldRoom = this.room;
        if (oldRoom != null) {
            oldRoom.getSchedules().remove(this);
        }

        this.room = newRoom;
        if (newRoom != null && !newRoom.getSchedules().contains(this)) {
            newRoom.getSchedules().add(this);
        }
    }
}

