package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int size;

    private String abbreviation;

    @OneToMany(mappedBy = "room")
    private List<ScheduleItem> schedules;

    // Getters et setters
}
