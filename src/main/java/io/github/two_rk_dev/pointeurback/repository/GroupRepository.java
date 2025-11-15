package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByLevelId(Long levelId);

    Group findByLevelIdAndId(Long levelId, Long groupId);

    boolean existsGroupByLevel_IdIs(Long levelId);
}
