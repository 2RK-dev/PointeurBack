package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record LevelDetailsDTO(
        LevelDTO level,
        List<GroupDTO> groups
) {
}
