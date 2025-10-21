package io.github.two_rk_dev.pointeurback.dto;

public record GroupDTO(
        Long id,
        String name,
        String type,
        String classe,
        Integer size,
        LevelDTO level
) {
}
