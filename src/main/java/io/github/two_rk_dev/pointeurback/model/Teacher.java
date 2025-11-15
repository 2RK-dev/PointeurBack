package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Teacher {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long id;

    @Setter
    private String name;

    @Setter
    private String abbreviation;

    @OneToMany(mappedBy = "teacher")
    private List<ScheduleItem> schedules;

}
