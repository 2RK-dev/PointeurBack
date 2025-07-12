package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record TeachingUnitDTO(
        Long id,
        String abbreviation,
        String name,
        List<LevelDTO> levels
) {
}

