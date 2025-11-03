package io.github.two_rk_dev.pointeurback.dto;

public record CreateGroupDTO(
        String name,
        String type,
        String classe,
        Integer size,
        /// For import, will not affect creation via HTTP request
        Long levelId
) {
}
