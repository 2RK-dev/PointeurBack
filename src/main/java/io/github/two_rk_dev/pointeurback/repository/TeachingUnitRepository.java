package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.TeachingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeachingUnitRepository extends JpaRepository<TeachingUnit, Long> {
    List<TeachingUnit> findByLevelId(Long LevelId);

    boolean existsByName(String name);

    boolean existsByAbbreviation(String abbreviation);
}