package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // Recherche par abréviation
    Optional<Group> findByAbbreviation(String abbreviation);

    // Recherche par niveau
    List<Group> findByLevelId(Long levelId);

}
