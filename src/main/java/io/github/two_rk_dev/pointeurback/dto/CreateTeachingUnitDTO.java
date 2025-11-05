package io.github.two_rk_dev.pointeurback.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CreateTeachingUnitDTO(
        @NotNull String abbreviation,
        @NotNull String name,
        @Nullable Long levelId
) {
}
