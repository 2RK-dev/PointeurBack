package io.github.two_rk_dev.pointeurback.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateTeacherDTO(
        @NotNull String name,
        @NotNull String abbreviation
) {
}
