package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ScheduleService {
    List<ScheduleItemDTO> getSchedule(@Nullable Long levelId, @Nullable Long groupId, String start, String endTime);

    ScheduleItemDTO updateScheduleItem(Long id, UpdateScheduleItemDTO dto);
    void deleteScheduleItem(Long id);

    ScheduleItemDTO getScheduleById(Long scheduleId);
}
