package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    // Recherche par nom de niveau
    Optional<Level> findByName(String name);

}