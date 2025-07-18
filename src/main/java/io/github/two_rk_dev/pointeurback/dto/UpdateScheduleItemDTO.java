package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record UpdateScheduleItemDTO(
        List<Long> groupIds,
        Long teacherId,
        Long teachingUnitId,
        Long roomId,
        String start,
        String endTime
) {
}
