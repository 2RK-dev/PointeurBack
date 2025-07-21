package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long id;

    private String name;

    private String abbreviation;

    @OneToMany(mappedBy = "teacher")
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

    public List<ScheduleItem> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleItem> schedules) {
        this.schedules = schedules != null ? schedules : new ArrayList<>();
    }

}
