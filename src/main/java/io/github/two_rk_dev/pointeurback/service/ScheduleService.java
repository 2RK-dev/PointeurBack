package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;

import java.util.List;

public interface ScheduleService {
    List<ScheduleItemDTO> getSchedule(String start,String end );
}
