package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.TeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.exception.LevelNotFoundException;
import io.github.two_rk_dev.pointeurback.exception.TeachingUnitNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.TeachingUnitMapper;
import io.github.two_rk_dev.pointeurback.model.Level;
import io.github.two_rk_dev.pointeurback.model.TeachingUnit;
import io.github.two_rk_dev.pointeurback.repository.LevelRepository;
import io.github.two_rk_dev.pointeurback.repository.TeachingUnitRepository;
import io.github.two_rk_dev.pointeurback.service.TeachingUnitService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TeachingUnitServiceImpl implements TeachingUnitService {

    private final TeachingUnitRepository teachingUnitRepository;
    private final LevelRepository levelRepository;
    private final TeachingUnitMapper teachingUnitMapper;
    public TeachingUnitServiceImpl(TeachingUnitRepository teachingUnitRepository, LevelRepository levelRepository, TeachingUnitMapper teachingUnitMapper) {
        this.teachingUnitRepository = teachingUnitRepository;
        this.levelRepository = levelRepository;
        this.teachingUnitMapper = teachingUnitMapper;
    }

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
        Objects.requireNonNull(dto, "CreateTeachingUnitDTO cannot be null");
        Level level = null;
        if (dto.levelId() != null) {
            level = levelRepository.findById(dto.levelId())
                    .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + dto.levelId()));
        }
        TeachingUnit newTeachingUnit = teachingUnitMapper.createTeachingUnitFromDto(dto, level);
        TeachingUnit savedTeachingUnit = teachingUnitRepository.save(newTeachingUnit);
        return teachingUnitMapper.toDto(savedTeachingUnit);
    }

    @Override
    public TeachingUnitDTO updateTeachingUnit(Long id, UpdateTeachingUnitDTO dto) {
        Objects.requireNonNull(dto, "UpdateTeachingUnitDTO cannot be null");
        TeachingUnit existingTeachingUnit = teachingUnitRepository.findById(id)
                .orElseThrow(() -> new TeachingUnitNotFoundException("Teaching unit not found with id: " + id));

        Level level = null;
        if (dto.levelId() != null) {
            level = levelRepository.findById(dto.levelId())
                    .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + dto.levelId()));
        }

        teachingUnitMapper.updateTeachingUnit(dto, existingTeachingUnit, level);
        TeachingUnit updatedTeachingUnit = teachingUnitRepository.save(existingTeachingUnit);
        return teachingUnitMapper.toDto(updatedTeachingUnit);
    }

    @Override
    public void deleteTeachingUnit(Long id) {
        teachingUnitRepository.deleteById(id);
    }

    @Override
    public void saveTeachingUnits(CreateTeachingUnitDTO[] teachingUnits) {
        if (teachingUnits == null) {
            throw new IllegalArgumentException("teachingUnits array cannot be null");
        }

        List<TeachingUnit> toSave = new java.util.ArrayList<>();
        for (CreateTeachingUnitDTO dto : teachingUnits) {
            if (dto == null) continue;
            Level level = null;
            if (dto.levelId() != null) {
                level = levelRepository.findById(dto.levelId()).orElse(null);
            }
            if (dto.name() == null) continue;
            if (teachingUnitRepository.existsByName(dto.name()) ||
                    (dto.abbreviation() != null && teachingUnitRepository.existsByAbbreviation(dto.abbreviation()))) {
                continue;
            }
            TeachingUnit tu = teachingUnitMapper.createTeachingUnitFromDto(dto, level);
            toSave.add(tu);
        }

        if (!toSave.isEmpty()) {
            teachingUnitRepository.saveAll(toSave);
        }
    }
}
