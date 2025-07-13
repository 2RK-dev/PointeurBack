package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;

import java.util.List;

public interface ScheduleService {
    List<ScheduleItemDTO> getSchedule(String start,String endTime );
    ScheduleItemDTO updateScheduleItem(Long id, UpdateScheduleItemDTO dto);
    Void deleteScheduleItem(Long id);

}
