package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
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

    // Getters et setters
}
