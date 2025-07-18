package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // Recherche par abréviation
    Optional<Teacher> findByAbbreviation(String abbreviation);

    // Recherche par nom
    List<Teacher> findByNameContainingIgnoreCase(String name);
}
