package io.github.two_rk_dev.pointeurback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLevelDTO(
        @NotBlank String name,
        @NotNull String abbreviation
) {
}
