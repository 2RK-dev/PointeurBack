package io.github.two_rk_dev.pointeurback.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateGroupDTO(
        @NotNull String name,
        @NotNull String type,
        @NotNull String classe,
        @NotNull @Positive Integer size
) {
}
