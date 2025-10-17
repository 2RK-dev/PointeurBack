package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.*;

import java.util.List;

public interface LevelService {
    LevelDTO createLevel(CreateLevelDTO dto);

    List<LevelDetailsDTO> getAllDetailed();

    List<LevelDTO> getAll();
    LevelDetailsDTO getDetails(Long Id);
    LevelDTO updateLevel(Long id, UpdateLevelDTO dto);
    void deleteLevel (Long id);
    List<GroupDTO> getGroups(Long id);
    List<TeachingUnitDTO> getTeachingUnits(Long id);
    List<ScheduleItemDTO> getSchedule(Long id);
    GroupDTO createGroup(Long id,CreateGroupDTO dto);

}
