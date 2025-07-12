package io.github.two_rk_dev.pointeurback.dto;

public record CreateTeachingUnitDTO(
        String abbreviation,
        String name,
        long levelId
) {
}
