package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.GroupMapper;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.repository.GroupRepository;
import io.github.two_rk_dev.pointeurback.repository.LevelRepository;
import io.github.two_rk_dev.pointeurback.repository.ScheduleItemRepository;
import io.github.two_rk_dev.pointeurback.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if (!existing.getSchedules().isEmpty()) {
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
    public void saveGroups(Map<Long, List<CreateGroupDTO>> groups) {
        if (groups == null || groups.isEmpty()) return;
        List<Group> toSave = new ArrayList<>();
        groups.forEach((levelId, groupsByLevelId) -> {
            for (CreateGroupDTO dto : groupsByLevelId) {
                if (dto == null) continue;
                if (dto.name() == null) continue;
                if (groupRepository.existsByName(dto.name())) continue;
                Group group = groupMapper.fromCreateDto(dto);
                if (dto.levelId() != null) {
                    levelRepository.findById(dto.levelId()).ifPresent(group::setLevel);
                }
                toSave.add(group);
            }
        });
        if (!toSave.isEmpty()) groupRepository.saveAll(toSave);
    }

    @Override
    public List<GroupDTO> getAll() {
        return groupMapper.toDtoList(groupRepository.findAll());
    }
}
