package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.BatchCreateResponse;
import io.github.two_rk_dev.pointeurback.dto.BatchCreateResponse.FailedItem;
import io.github.two_rk_dev.pointeurback.dto.CreateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.exception.ScheduleConflictException;
import io.github.two_rk_dev.pointeurback.exception.ScheduleItemNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.ScheduleItemMapper;
import io.github.two_rk_dev.pointeurback.model.Room;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.repository.*;
import io.github.two_rk_dev.pointeurback.service.ScheduleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final TeachingUnitRepository teachingUnitRepository;
    private final RoomRepository roomRepository;
    private final ScheduleItemMapper scheduleItemMapper;
    private final EntityManager entityManager;

    @Override
    public List<ScheduleItemDTO> getSchedule(@Nullable Long levelId, @Nullable Long groupId, String start,
                                             String endTime) {
        OffsetDateTime startDateTime = scheduleItemMapper.parseDateTime(start);
        OffsetDateTime endDateTime = scheduleItemMapper.parseDateTime(endTime);

        List<ScheduleItem> items;
        if (groupId != null) {
            if (levelId != null && !groupRepository.existsGroupByLevel_IdIs(levelId)) {
                throw new GroupNotFoundException("Group with id " + groupId + " not found in level " + levelId);
            }
            items = scheduleItemRepository.findByGroupsId(groupId);
        } else if (levelId != null) {
            items = scheduleItemRepository.findByLevelId(levelId);
        } else
            items = scheduleItemRepository.findByStartTimeBetween(startDateTime, endDateTime);
        return scheduleItemMapper.toDtoList(items);
    }

    @Override
    @Transactional
    public ScheduleItemDTO updateScheduleItem(Long id, UpdateScheduleItemDTO dto) {
        ScheduleItem existingItem = scheduleItemRepository.findById(id)
                .orElseThrow(() -> new ScheduleItemNotFoundException("Schedule item not found with id: " + id));
        entityManager.setFlushMode(FlushModeType.COMMIT);
        scheduleItemMapper.updateFromDto(
                dto,
                existingItem,
                groupRepository::findAllById,
                teacherRepository::findById,
                teachingUnitRepository::findById,
                roomRepository::findById);

        try {
            List<ScheduleItem> conflictingItems = scheduleItemRepository.findConflictingSchedule(
                    existingItem.getStartTime(),
                    existingItem.getEndTime(),
                    Optional.ofNullable(existingItem.getRoom()).map(Room::getId).orElse(null),
                    existingItem.getTeacher().getId(),
                    dto.groupIds());
            conflictingItems.removeIf(si -> si.getId().equals(existingItem.getId()));
            if (!conflictingItems.isEmpty()) {
                throw new ScheduleConflictException(existingItem, conflictingItems);
            }
        } finally {
            entityManager.setFlushMode(FlushModeType.AUTO);
        }

        ScheduleItem updatedItem = scheduleItemRepository.save(existingItem);
        return scheduleItemMapper.toDto(updatedItem);
    }

    @Override
    public void deleteScheduleItem(Long id) {
        scheduleItemRepository.deleteById(id);
    }

    @Override
    public ScheduleItemDTO addScheduleItem(CreateScheduleItemDTO dto) {
        ScheduleItem newItem = scheduleItemMapper.createFromDto(
                dto,
                groupRepository::findById,
                teacherRepository::findById,
                teachingUnitRepository::findById,
                roomRepository::findById);

        List<ScheduleItem> conflictingItems = scheduleItemRepository.findConflictingSchedule(
                newItem.getStartTime(),
                newItem.getEndTime(),
                Optional.ofNullable(newItem.getRoom()).map(Room::getId).orElse(null),
                newItem.getTeacher().getId(),
                dto.groupIds());
        if (!conflictingItems.isEmpty()) {
            throw new ScheduleConflictException(newItem, conflictingItems);
        }

        ScheduleItem savedItem = scheduleItemRepository.save(newItem);
        return scheduleItemMapper.toDto(savedItem);
    }

    @Override
    public ScheduleItemDTO getScheduleById(Long scheduleId) {
        return null;
    }

    @Override
    public BatchCreateResponse<ScheduleItemDTO, CreateScheduleItemDTO> addScheduleItems(@NotNull List<CreateScheduleItemDTO> dtos) {
        List<ScheduleItemDTO> successful = new ArrayList<>();
        List<FailedItem<CreateScheduleItemDTO>> failed = new ArrayList<>();
        for (CreateScheduleItemDTO dto : dtos) {
            try {
                successful.add(addScheduleItem(dto));
            } catch (Exception e) {
                failed.add(new FailedItem<>(dto, e.getMessage()));
            }
        }
        return new BatchCreateResponse<>(successful, failed);
    }
}
