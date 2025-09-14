package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.exception.ScheduleItemNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.ScheduleItemMapper;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.repository.*;
import io.github.two_rk_dev.pointeurback.service.ScheduleService;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final TeachingUnitRepository teachingUnitRepository;
    private final RoomRepository roomRepository;
    private final ScheduleItemMapper scheduleItemMapper;
    public ScheduleServiceImpl(ScheduleItemRepository scheduleItemRepository, GroupRepository groupRepository, TeacherRepository teacherRepository, TeachingUnitRepository teachingUnitRepository, RoomRepository roomRepository, ScheduleItemMapper scheduleItemMapper) {
        this.scheduleItemRepository = scheduleItemRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
        this.teachingUnitRepository = teachingUnitRepository;
        this.roomRepository = roomRepository;
        this.scheduleItemMapper = scheduleItemMapper;
    }

    @Override
    public List<ScheduleItemDTO> getSchedule(@Nullable Long levelId, @Nullable Long groupId, String start, String endTime) {
        OffsetDateTime startDateTime = scheduleItemMapper.parseDateTime(start);
        OffsetDateTime endDateTime = scheduleItemMapper.parseDateTime(endTime);

        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start and end times must be provided");
        }
        List<ScheduleItem> items;
        if (groupId != null) {
            if (levelId != null && !groupRepository.existsGroupByLevel_IdIs(levelId)) {
                throw new GroupNotFoundException("Group with id " + groupId + " not found in level " + levelId);
            }
            items = scheduleItemRepository.findByGroupsId(groupId);
        } else if (levelId != null) {
            items = scheduleItemRepository.findByLevelId(levelId);
        } else items = scheduleItemRepository.findByStartTimeBetween(startDateTime, endDateTime);
        return scheduleItemMapper.toDtoList(items);
    }

    @Override
    public ScheduleItemDTO updateScheduleItem(Long id, UpdateScheduleItemDTO dto) {
        ScheduleItem existingItem = scheduleItemRepository.findById(id)
                .orElseThrow(() -> new ScheduleItemNotFoundException("Schedule item not found with id: " + id));

        OffsetDateTime newStart = scheduleItemMapper.parseDateTime(dto.startTime());
        OffsetDateTime newEnd = scheduleItemMapper.parseDateTime(dto.endTime());
        List<ScheduleItem> conflictingItems = scheduleItemRepository.findConflictingSchedule(
                newStart,
                newEnd,
                existingItem.getRoom().getId(),
                existingItem.getTeacher().getId(),
                dto.groupIds()
        );
        conflictingItems.removeIf(si -> si.getId().equals(existingItem.getId()));
        if (!conflictingItems.isEmpty()) {
            throw new IllegalStateException("Schedule conflict detected");
        }

        scheduleItemMapper.updateFromDto(
                dto,
                existingItem,
                groupRepository::findAllById,
                teacherId -> teacherRepository.findById(teacherId).orElse(null),
                teachingUnitId -> teachingUnitRepository.findById(teachingUnitId).orElse(null),
                roomId -> roomRepository.findById(roomId).orElse(null)
        );

        ScheduleItem updatedItem = scheduleItemRepository.save(existingItem);
        return scheduleItemMapper.toDto(updatedItem);
    }

    @Override
    public void deleteScheduleItem(Long id) {
        Optional<ScheduleItem> item = scheduleItemRepository.findById(id);
        item.ifPresent(scheduleItemRepository::delete);
    }

    public ScheduleItemDTO addScheduleItem(CreateScheduleItemDTO dto) {
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

    @Override
    public ScheduleItemDTO getScheduleById(Long scheduleId) {
        return null;
    }
}
