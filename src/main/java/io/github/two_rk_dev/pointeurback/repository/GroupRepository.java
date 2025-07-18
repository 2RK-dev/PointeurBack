package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // Recherche par niveau
    List<Group> findByLevelId(Long levelId);

    // Recherche par nom
    Group findByName(String name);


    // Recherche par niveau et id
    Group findByLevelIdAndId(Long levelId, Long groupId);

}
