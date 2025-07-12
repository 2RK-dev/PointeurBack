package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record CreateTeachingUnitDTO(
        String abbreviation,
        String name,
        List<Long> levelIds
) {
}
