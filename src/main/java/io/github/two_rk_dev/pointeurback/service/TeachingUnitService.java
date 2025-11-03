package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.TeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportTeachingUnitDTO;

import java.util.List;

public interface TeachingUnitService {
    List<TeachingUnitDTO> getAll();

    TeachingUnitDTO getTeachingUnit(Long id);

    TeachingUnitDTO createTeachingUnit(CreateTeachingUnitDTO dto);

    TeachingUnitDTO updateTeachingUnit(Long id, UpdateTeachingUnitDTO dto);

    void deleteTeachingUnit(Long id);

    List<ImportTeachingUnitDTO> exportAll();
}
