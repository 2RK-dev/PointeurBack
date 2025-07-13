package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.*;

import java.util.List;

public interface LevelService {
    LevelDTO createLevel(CreateLevelDTO dto);
    List<LevelDTO> getAll();
    LevelDetailsDTO getDetails(Long Id);
    LevelDTO updateLevel(Long id, UpdateLevelDTO dto);
    Void deleteLevel (Long id);
    List<GroupDTO> getGroup(Long id);
    List<TeachingUnitDTO> getTeachingUnit(Long id);
    ScheduleItemDTO getSchedule(Long id);
    GroupDTO createGroup(CreateGroupDTO dto);
}
