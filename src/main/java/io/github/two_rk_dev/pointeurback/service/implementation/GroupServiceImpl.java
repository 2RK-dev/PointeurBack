package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportGroupDTO;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.GroupMapper;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.repository.GroupRepository;
import io.github.two_rk_dev.pointeurback.repository.ScheduleItemRepository;
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

    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper, ScheduleItemRepository scheduleRepository) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.scheduleRepository = scheduleRepository;
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
    public List<ImportGroupDTO> exportAll() {
        return groupRepository.findAll().stream().map(groupMapper::toExportDTO).toList();
    }
}
