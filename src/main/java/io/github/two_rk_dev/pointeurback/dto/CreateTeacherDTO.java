package io.github.two_rk_dev.pointeurback.dto;

import jakarta.validation.constraints.NotNull;

public record CreateTeacherDTO(
        @NotNull String name,
        @NotNull String abbreviation
) {
}
