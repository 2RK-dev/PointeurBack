package io.github.two_rk_dev.pointeurback.dto.datasync;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ImportRoomDTO(
        @NotNull Long id,
        @NotNull String name,
        @NotNull String abbreviation,
        @Positive Integer size
) {
}
