package io.github.two_rk_dev.pointeurback.dto.datasync;

import jakarta.validation.constraints.NotNull;

public record ImportTeacherDTO(
        @NotNull Long id,
        @NotNull String name,
        @NotNull String abbreviation
) {
}
