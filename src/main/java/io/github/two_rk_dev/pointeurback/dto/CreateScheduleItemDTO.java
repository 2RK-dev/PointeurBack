package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record CreateScheduleItemDTO(
        List<Long> groupIds,
        Long teacherId,
        Long teachingUnitId,
        Long roomId,
        String startTime,
        String endTime
) {
}
