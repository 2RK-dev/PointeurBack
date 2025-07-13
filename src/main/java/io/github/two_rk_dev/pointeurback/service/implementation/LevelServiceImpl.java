package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.*;
import io.github.two_rk_dev.pointeurback.exception.LevelNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.GroupMapper;
import io.github.two_rk_dev.pointeurback.mapper.LevelMapper;
import io.github.two_rk_dev.pointeurback.mapper.ScheduleItemMapper;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.Level;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.model.TeachingUnit;
import io.github.two_rk_dev.pointeurback.repository.*;
import io.github.two_rk_dev.pointeurback.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevelServiceImpl implements LevelService {

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TeacherRepository teacherRepository;


    @Autowired
    private TeachingUnitRepository teachingUnitRepository;

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private ScheduleItemMapper scheduleItemMapper;

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
    public Void deleteLevel(Long id) {
        // 1. Vérification existence
        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + id));

        // 2. Vérification des contraintes
        if (!existing.getGroups().isEmpty()) {
            throw new RuntimeException("Impossible à Supprimmer");
        }

        // 3. Suppression
        levelRepository.delete(existing);
        return null;
    }

    public List<GroupDTO> getGroup(Long levelId){
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
    public List<TeachingUnitDTO> getTeachingUnit(Long levelId){
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
        // 1. Validation du DTO
        if (dto == null) {
            throw new IllegalArgumentException("CreateGroupDTO cannot be null");
        }

        // 2. Validation des champs obligatoires
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalStateException("Group name cannot be empty");
        }
        if (dto.size() <= 0) {
            throw new IllegalStateException("Group size must be positive");
        }

        // 3. Vérification de l'existence du niveau associé
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + levelId));

        // 4. Conversion via le mapper
        Group newGroup = groupMapper.fromCreateDto(dto, level);

        // 5. Validation métier supplémentaire
        if (groupRepository.findByName(dto.name()) == null) {
            throw new RuntimeException("Group name already exists for this level");
        }

        // 6. Persistance
        Group savedGroup = groupRepository.save(newGroup);

        // 7. Conversion en DTO pour la réponse
        return groupMapper.toDto(savedGroup);
    }

    public ScheduleItemDTO addScheduleItem(Long levelId, CreateScheduleItemDTO dto){
        // 1. Vérification de l'existence du niveau
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + levelId));

        // 2. Validation du DTO
        if (dto == null) {
            throw new IllegalArgumentException("CreateScheduleItemDTO cannot be null");
        }

        // 3. Création de l'entité ScheduleItem avec les dépendances
        ScheduleItem newItem = scheduleItemMapper.createFromDto(
                dto,
                groupId -> groupRepository.findById(groupId).orElse(null),
                teacherId -> teacherRepository.findById(teacherId).orElse(null),
                teachingUnitId -> teachingUnitRepository.findById(teachingUnitId).orElse(null),
                roomId -> roomRepository.findById(roomId).orElse(null)
        );

        // 4 .Vérification des conflits d'horaire
        List<ScheduleItem> conflictingItems = scheduleItemRepository.findConflictingSchedule(
                newItem.getStart(),
                newItem.getEndTime(),
                newItem.getRoom().getId(),
                newItem.getTeacher().getId(),
                dto.groupIds()
        );
        if (!conflictingItems.isEmpty()) {
            throw new IllegalStateException("Schedule conflict detected");
        }

        // 5. Sauvegarde
        ScheduleItem savedItem = scheduleItemRepository.save(newItem);

        // 6. Conversion en DTO
        return scheduleItemMapper.toDto(savedItem);
    };
}
