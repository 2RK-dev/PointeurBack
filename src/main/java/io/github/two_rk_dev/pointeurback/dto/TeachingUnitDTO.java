package io.github.two_rk_dev.pointeurback.dto;

public record TeachingUnitDTO(
        Long id,
        String abbreviation,
        String name,
        LevelDTO level
) {
}
