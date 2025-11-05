package io.github.two_rk_dev.pointeurback.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateRoomDTO(
        @NotNull String name,
        @NotNull String abbreviation,
        @Positive int size
) {
}
