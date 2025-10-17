package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByName(String name);
    boolean existsByAbbreviation(String abbreviation);
}
