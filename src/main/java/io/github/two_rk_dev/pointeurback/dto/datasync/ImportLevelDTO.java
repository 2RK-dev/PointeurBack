package io.github.two_rk_dev.pointeurback.dto.datasync;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImportLevelDTO(
        @NotNull Long id,
        @NotBlank String name,
        @NotNull String abbreviation
) {
}
