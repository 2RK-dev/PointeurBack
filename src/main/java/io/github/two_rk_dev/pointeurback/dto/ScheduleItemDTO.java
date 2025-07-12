package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record ScheduleItemDTO(
        Long id,
        List<GroupDTO> groups,
        TeacherDTO teacher,
        TeachingUnitDTO teachingUnit,
        RoomDTO room,
        String start,
        String end
) {
}
