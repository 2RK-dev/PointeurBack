package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.TeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.exception.TeachingUnitNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.TeachingUnitMapper;
import io.github.two_rk_dev.pointeurback.model.Level;
import io.github.two_rk_dev.pointeurback.model.TeachingUnit;
import io.github.two_rk_dev.pointeurback.repository.LevelRepository;
import io.github.two_rk_dev.pointeurback.repository.TeachingUnitRepository;
import io.github.two_rk_dev.pointeurback.service.TeachingUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachingUnitServiceImpl implements TeachingUnitService {

    @Autowired
    private TeachingUnitRepository teachingUnitRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TeachingUnitMapper teachingUnitMapper;

    @Override
    public List<TeachingUnitDTO> getAll() {
        List<TeachingUnit> teachingUnits = teachingUnitRepository.findAll();
        return teachingUnitMapper.toDto(teachingUnits);
    }

    @Override
    public TeachingUnitDTO getTeachingUnit(Long id) {
        TeachingUnit teachingUnit = teachingUnitRepository.findById(id)
                .orElseThrow(() -> new TeachingUnitNotFoundException("Teaching unit not found with id: " + id));
        return teachingUnitMapper.toDto(teachingUnit);
    }

    @Override
    public TeachingUnitDTO createTeachingUnit(CreateTeachingUnitDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("CreateTeachingUnitDTO cannot be null");
        }

        Level level = levelRepository.findById(dto.levelId())
                .orElseThrow(() -> new IllegalArgumentException("Level not found with id: " + dto.levelId()));

        TeachingUnit newTeachingUnit = teachingUnitMapper.createTeachingUnitFromDto(dto, level);
        TeachingUnit savedTeachingUnit = teachingUnitRepository.save(newTeachingUnit);
        return teachingUnitMapper.toDto(savedTeachingUnit);
    }

    @Override
    public TeachingUnitDTO updateTeachingUnit(Long id, UpdateTeachingUnitDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UpdateTeachingUnitDTO cannot be null");
        }

        TeachingUnit existingTeachingUnit = teachingUnitRepository.findById(id)
                .orElseThrow(() -> new TeachingUnitNotFoundException("Teaching unit not found with id: " + id));

        Level level = null;
        if (dto.levelId() < 0 ) level = existingTeachingUnit.getLevel();

        level = levelRepository.findById(dto.levelId())
                .orElseThrow(() -> new IllegalArgumentException("Level not found with id: " + dto.levelId()));


        teachingUnitMapper.updateTeachingUnit(dto, existingTeachingUnit, level);
        TeachingUnit updatedTeachingUnit = teachingUnitRepository.save(existingTeachingUnit);
        return teachingUnitMapper.toDto(updatedTeachingUnit);
    }

    @Override
    public Void deleteTeachingUnit(Long id) {
        TeachingUnit teachingUnit = teachingUnitRepository.findById(id)
                .orElseThrow(() -> new TeachingUnitNotFoundException("Teaching unit not found with id: " + id));

        if (!teachingUnit.getSchedules().isEmpty()) {
            throw new IllegalStateException("Cannot delete teaching unit with associated schedules");
        }

        teachingUnitRepository.delete(teachingUnit);
        return null;
    }
}
