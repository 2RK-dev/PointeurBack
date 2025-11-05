package io.github.two_rk_dev.pointeurback.dto.datasync;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ImportGroupDTO(
        @NotNull Long id,
        @NotNull String name,
        @Positive Integer size,
        Long levelId,
        @NotNull String type,
        @NotNull String classe
) {
}
