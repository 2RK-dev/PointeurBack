package io.github.two_rk_dev.pointeurback.dto.datasync;

import jakarta.validation.constraints.NotNull;

public record ImportTeachingUnitDTO(
        @NotNull Long id,
        @NotNull String abbreviation,
        @NotNull String name,
        Long levelId
) {
}
