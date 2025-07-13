package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // Ex: "L1", "L2", "M1", etc.

    private String abbreviation;

    @OneToMany(mappedBy = "level")
    private List<Group> groups;
}