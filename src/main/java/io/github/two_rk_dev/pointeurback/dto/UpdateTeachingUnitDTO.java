package io.github.two_rk_dev.pointeurback.dto;

import jakarta.annotation.Nullable;

public record UpdateTeachingUnitDTO(
        String abbreviation,
        String name,
        @Nullable
        Long levelId
) {
}
