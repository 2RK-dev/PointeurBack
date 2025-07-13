package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.TeachingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeachingUnitRepository extends JpaRepository<TeachingUnit, Long> {
    // Recherche par abréviation
    Optional<TeachingUnit> findByAbbreviation(String abbreviation);

    // Recherche par nom
    List<TeachingUnit> findByNameContainingIgnoreCase(String name);
}