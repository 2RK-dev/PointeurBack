package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.*;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportLevelDTO;

import java.util.List;
import java.util.stream.Stream;

public interface LevelService {
    LevelDTO createLevel(CreateLevelDTO dto);

    void importLevels(Stream<ImportLevelDTO> dtoStream);
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
