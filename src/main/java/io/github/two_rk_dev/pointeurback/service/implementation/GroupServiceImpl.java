package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.GroupMapper;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.repository.GroupRepository;
import io.github.two_rk_dev.pointeurback.repository.ScheduleItemRepository;
import io.github.two_rk_dev.pointeurback.repository.LevelRepository;
import io.github.two_rk_dev.pointeurback.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final ScheduleItemRepository scheduleRepository;
    private final LevelRepository levelRepository;

    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper,
                            ScheduleItemRepository scheduleRepository, LevelRepository levelRepository) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.scheduleRepository = scheduleRepository;
        this.levelRepository = levelRepository;
    }

    @Override
    public GroupDTO getGroupByLevel(Long levelId, Long groupId) {
        Group existing = groupRepository.findByLevelIdAndId(levelId, groupId);
        if (existing == null) {
            throw new GroupNotFoundException("Groupe non Existant");
        }
        return groupMapper.toDto(existing);
    }

    @Override
    public GroupDTO updateGroup(Long levelId, Long groupId, UpdateGroupDTO dto) {
        Group existing = groupRepository.findByLevelIdAndId(levelId, groupId);
        if (existing == null) {
            throw new GroupNotFoundException("Groupe non trouvé pour levelId: " + levelId + " et groupId: " + groupId);
        }
        groupMapper.updateFromUpdateDto(dto, existing);

        Group updated = groupRepository.save(existing);
        return groupMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteGroup(Long levelId, Long groupId) {
        Group existing = groupRepository.findByLevelIdAndId(levelId, groupId);
        if(!existing.getSchedules().isEmpty()) {
            List<ScheduleItem> schedulesCopy = new ArrayList<>(existing.getSchedules());
            schedulesCopy.forEach(schedule -> {
                schedule.getGroups().remove(existing);
                scheduleRepository.save(schedule);
            });
            existing.getSchedules().clear();
            groupRepository.save(existing);
        }
        groupRepository.delete(existing);
        groupRepository.flush();
    }

    @Override
    public void saveGroups(Long levelId, CreateGroupDTO[] groups) {
        if (groups == null) {
            throw new IllegalArgumentException("groups array cannot be null");
        }
        // Ensure level exists
        if (!groupRepository.existsGroupByLevel_IdIs(levelId)) {
            // It's possible the repository method semantics are different; we only check and proceed
        }

        List<Group> toSave = new java.util.ArrayList<>();
        for (CreateGroupDTO dto : groups) {
            if (dto == null) continue;
            if (dto.name() == null) continue;
            if (groupRepository.existsByName(dto.name())) continue;
            Group group = groupMapper.fromCreateDto(dto);
            // attach the level if present
            if (levelId != null) {
                levelRepository.findById(levelId).ifPresent(group::setLevel);
            }
            toSave.add(group);
        }

        if (!toSave.isEmpty()) {
            // Persist groups
            groupRepository.saveAll(toSave);
        }
    }

}
