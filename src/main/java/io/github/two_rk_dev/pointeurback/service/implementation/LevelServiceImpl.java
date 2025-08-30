package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.*;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.exception.LevelNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.GroupMapper;
import io.github.two_rk_dev.pointeurback.mapper.LevelMapper;
import io.github.two_rk_dev.pointeurback.mapper.ScheduleItemMapper;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.Level;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.model.TeachingUnit;
import io.github.two_rk_dev.pointeurback.repository.GroupRepository;
import io.github.two_rk_dev.pointeurback.repository.LevelRepository;
import io.github.two_rk_dev.pointeurback.repository.ScheduleItemRepository;
import io.github.two_rk_dev.pointeurback.repository.TeachingUnitRepository;
import io.github.two_rk_dev.pointeurback.service.LevelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    private final GroupRepository groupRepository;
    private final TeachingUnitRepository teachingUnitRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final LevelMapper levelMapper;
    private final GroupMapper groupMapper;
    private final ScheduleItemMapper scheduleItemMapper;

    public LevelServiceImpl(LevelRepository levelRepository, GroupRepository groupRepository, TeachingUnitRepository teachingUnitRepository, ScheduleItemRepository scheduleItemRepository, LevelMapper levelMapper, GroupMapper groupMapper, ScheduleItemMapper scheduleItemMapper) {
        this.levelRepository = levelRepository;
        this.groupRepository = groupRepository;
        this.teachingUnitRepository = teachingUnitRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.levelMapper = levelMapper;
        this.groupMapper = groupMapper;
        this.scheduleItemMapper = scheduleItemMapper;
    }
    @Override
    public LevelDTO createLevel(CreateLevelDTO dto) {
        // Validation du DTO
        if (dto == null) {
            throw new IllegalArgumentException("CreateLevelDTO cannot be null");
        }

        // Conversion en entité
        Level newLevel = levelMapper.fromCreateDto(dto);

        // Validation métier supplémentaire
        if (newLevel.getName() == null || newLevel.getName().isBlank()) {
            throw new IllegalStateException("Level name cannot be empty");
        }

        // Sauvegarde
        Level savedLevel = levelRepository.save(newLevel);
        return levelMapper.toDto(savedLevel);
    }

    @Override
    public List<LevelDTO> getAll() {
        List<Level> existing = levelRepository.findAll();

        // Utilisation de la méthode du mapper pour la conversion
        return levelMapper.toDtoList(existing);
    }

    @Override
    public LevelDetailsDTO getDetails(Long id) {
        // Recherche avec gestion d'erreur améliorée
        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + id));

        // Conversion détaillée avec les groupes
        LevelDetailsDTO detailsDto = levelMapper.toDetailsDto(existing);

        // Optionnel: Chargement supplémentaire si nécessaire
        if (detailsDto.groups() == null) {
            List<Group> groups = groupRepository.findByLevelId(id);
            detailsDto = new LevelDetailsDTO(
                    levelMapper.toDto(existing),
                    groups.stream()
                            .map(g -> new GroupDTO(g.getId(), g.getName(), g.getSize(), null))
                            .toList()
            );
        }

        return detailsDto;
    }
    @Override
    public LevelDTO updateLevel(Long id, UpdateLevelDTO dto) {
        // 1. Validation du DTO
        if (dto == null) {
            throw new IllegalArgumentException("UpdateLevelDTO cannot be null");
        }

        // 2. Recherche de l'entité existante
        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + id));

        // 3. Mise à jour via le mapper
        levelMapper.updateLevel(dto, existing);

        // 4. Sauvegarde et conversion
        Level updated = levelRepository.save(existing);
        return levelMapper.toDto(updated);
    }

    @Override
    public void deleteLevel(Long id) {
        // 1. Vérification existence
        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + id));

        // 2. Vérification des contraintes
        if (!existing.getGroups().isEmpty()) {
            throw new RuntimeException("Impossible à Supprimmer");
        }

        // 3. Suppression
        levelRepository.delete(existing);
    }

    @Override
    public List<GroupDTO> getGroups(Long levelId){
        // 1. Vérification existence niveau
        if (!levelRepository.existsById(levelId)) {
            throw new LevelNotFoundException("Level not found with id: " + levelId);
        }

        // 2. Récupération des groupes
        List<Group> groups = groupRepository.findByLevelId(levelId);

        // 3. Conversion via le mapper
        return groups.stream()
                .map(group -> new GroupDTO(
                        group.getId(),
                        group.getName(),
                        group.getSize(),
                        null)) // Pas de LevelDTO pour éviter les références circulaires
                .toList();
    };

    @Override
    public List<TeachingUnitDTO> getTeachingUnits(Long levelId){
        // 1. Vérification existence niveau
        if (!levelRepository.existsById(levelId)) {
            throw new LevelNotFoundException("Level not found with id: " + levelId);
        }

        // 2. Récupération des Matières
        List<TeachingUnit> teachingUnits = teachingUnitRepository.findByLevelId(levelId);

        // 3. Conversion via le mapper
        return teachingUnits.stream()
                .map(teachingUnit -> new TeachingUnitDTO(
                        teachingUnit.getId(),
                        teachingUnit.getAbbreviation(),
                        teachingUnit.getName(),
                        null)) // Pas de LevelDTO pour éviter les références circulaires
                .toList();
    };

    @Override
    public List<ScheduleItemDTO> getSchedule(Long levelId){
        // 1. Vérification existence niveau
        if (!levelRepository.existsById(levelId)) {
            throw new LevelNotFoundException("Level not found with id: " + levelId);
        }

        // 2. Récupération des Matières
        List<ScheduleItem> schedules = scheduleItemRepository.findByLevelId(levelId);

        // 3. Conversion via le mapper
        return schedules.stream()
                .map(scheduleItemMapper::toDto)
                .collect(Collectors.toList());


    };

    @Override
    public GroupDTO createGroup(Long levelId,CreateGroupDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("CreateGroupDTO cannot be null");
        }
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + levelId));
        Group newGroup = groupMapper.fromCreateDto(dto, level);
        if (groupRepository.findByName(dto.name()) != null) {
            Group savedGroup = groupRepository.save(newGroup);
            return groupMapper.toDto(savedGroup);
        } else {
            throw new GroupNotFoundException("Group name already exists for this level");
        }
    }
}
