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
import io.github.two_rk_dev.pointeurback.repository.*;
import io.github.two_rk_dev.pointeurback.service.LevelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final TeacherRepository teacherRepository;
    private final TeachingUnitRepository teachingUnitRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final LevelMapper levelMapper;
    private final GroupMapper groupMapper;
    private final ScheduleItemMapper scheduleItemMapper;

    public LevelServiceImpl(LevelRepository levelRepository, GroupRepository groupRepository, RoomRepository roomRepository, TeacherRepository teacherRepository, TeachingUnitRepository teachingUnitRepository, ScheduleItemRepository scheduleItemRepository, LevelMapper levelMapper, GroupMapper groupMapper, ScheduleItemMapper scheduleItemMapper) {
        this.levelRepository = levelRepository;
        this.groupRepository = groupRepository;
        this.roomRepository = roomRepository;
        this.teacherRepository = teacherRepository;
        this.teachingUnitRepository = teachingUnitRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.levelMapper = levelMapper;
        this.groupMapper = groupMapper;
        this.scheduleItemMapper = scheduleItemMapper;
    }
    @Override
    public LevelDTO createLevel(CreateLevelDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("CreateLevelDTO cannot be null");
        }
        Level newLevel = levelMapper.fromCreateDto(dto);
        if (newLevel.getName() == null || newLevel.getName().isBlank()) {
            throw new IllegalStateException("Level name cannot be empty");
        }
        Level savedLevel = levelRepository.save(newLevel);
        return levelMapper.toDto(savedLevel);
    }

    @Override
    public List<LevelDTO> getAll() {
        List<Level> existing = levelRepository.findAll();
        return levelMapper.toDtoList(existing);
    }

    @Override
    public LevelDetailsDTO getDetails(Long id) {
        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + id));
        LevelDetailsDTO detailsDto = levelMapper.toDetailsDto(existing);

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
        if (dto == null) {
            throw new IllegalArgumentException("UpdateLevelDTO cannot be null");
        }
        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + id));
        levelMapper.updateLevel(dto, existing);
        Level updated = levelRepository.save(existing);
        return levelMapper.toDto(updated);
    }

    @Override
    public void deleteLevel(Long id) {
        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new LevelNotFoundException("Level not found with id: " + id));
        if (!existing.getGroups().isEmpty()) {
            throw new RuntimeException("Impossible à Supprimer");
        }
        levelRepository.delete(existing);
    }

    @Override
    public List<GroupDTO> getGroups(Long levelId){
        if (!levelRepository.existsById(levelId)) {
            throw new LevelNotFoundException("Level not found with id: " + levelId);
        }

        List<Group> groups = groupRepository.findByLevelId(levelId);
        return groups.stream()
                .map(group -> new GroupDTO(
                        group.getId(),
                        group.getName(),
                        group.getSize(),
                        null))
                .toList();
    }

    @Override
    public List<TeachingUnitDTO> getTeachingUnits(Long levelId){
        if (!levelRepository.existsById(levelId)) {
            throw new LevelNotFoundException("Level not found with id: " + levelId);
        }
        List<TeachingUnit> teachingUnits = teachingUnitRepository.findByLevelId(levelId);
        return teachingUnits.stream()
                .map(teachingUnit -> new TeachingUnitDTO(
                        teachingUnit.getId(),
                        teachingUnit.getAbbreviation(),
                        teachingUnit.getName(),
                        null))
                .toList();
    }

    @Override
    public List<ScheduleItemDTO> getSchedule(Long levelId){
        if (!levelRepository.existsById(levelId)) {
            throw new LevelNotFoundException("Level not found with id: " + levelId);
        }
        List<ScheduleItem> schedules = scheduleItemRepository.findByLevelId(levelId);
        return schedules.stream()
                .map(scheduleItemMapper::toDto)
                .collect(Collectors.toList());
    }

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

    public ScheduleItemDTO addScheduleItem(Long levelId, CreateScheduleItemDTO dto){
        if (!levelRepository.existsById(levelId))
            throw new LevelNotFoundException("Level not found with id: " + levelId);

        if (dto == null) {
            throw new IllegalArgumentException("CreateScheduleItemDTO cannot be null");
        }

        ScheduleItem newItem = scheduleItemMapper.createFromDto(
                dto,
                groupId -> groupRepository.findById(groupId).orElse(null),
                teacherId -> teacherRepository.findById(teacherId).orElse(null),
                teachingUnitId -> teachingUnitRepository.findById(teachingUnitId).orElse(null),
                roomId -> roomRepository.findById(roomId).orElse(null)
        );

        List<ScheduleItem> conflictingItems = scheduleItemRepository.findConflictingSchedule(
                newItem.getStartTime(),
                newItem.getEndTime(),
                newItem.getRoom().getId(),
                newItem.getTeacher().getId(),
                dto.groupIds()
        );
        if (!conflictingItems.isEmpty()) {
            throw new IllegalStateException("Schedule conflict detected");
        }

        ScheduleItem savedItem = scheduleItemRepository.save(newItem);
        return scheduleItemMapper.toDto(savedItem);
    }
}
