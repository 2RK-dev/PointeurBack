package io.github.two_rk_dev.pointeurback.dto;

public record UpdateTeachingUnitDTO(
        String abbreviation,
        String name,
        long levelId
) {
}
