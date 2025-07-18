package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.exception.ScheduleItemNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.ScheduleItemMapper;
import io.github.two_rk_dev.pointeurback.model.ScheduleItem;
import io.github.two_rk_dev.pointeurback.repository.*;
import io.github.two_rk_dev.pointeurback.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private TeachingUnitRepository teachingUnitRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ScheduleItemMapper scheduleItemMapper;

    @Override
    public List<ScheduleItemDTO> getSchedule(String start, String endTime) {
        LocalDateTime startDateTime = scheduleItemMapper.parseDateTime(start);
        LocalDateTime endDateTime = scheduleItemMapper.parseDateTime(endTime);

        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start and end times must be provided");
        }

        List<ScheduleItem> items = scheduleItemRepository.findByStartBetween(startDateTime, endDateTime);
        return scheduleItemMapper.toDtoList(items);
    }

    @Override
    public ScheduleItemDTO updateScheduleItem(Long id, UpdateScheduleItemDTO dto) {
        ScheduleItem existingItem = scheduleItemRepository.findById(id)
                .orElseThrow(() -> new ScheduleItemNotFoundException("Schedule item not found with id: " + id));

        // Vérification des conflits avant mise à jour
        List<ScheduleItem> conflictingItems = scheduleItemRepository.findConflictingSchedule(
                existingItem.getStart(),
                existingItem.getEndTime(),
                existingItem.getRoom().getId(),
                existingItem.getTeacher().getId(),
                dto.groupIds()
        );
        if (!conflictingItems.isEmpty()) {
            throw new IllegalStateException("Schedule conflict detected");
        }

        scheduleItemMapper.updateFromDto(
                dto,
                existingItem,
                groupIds -> groupRepository.findAllById(groupIds),
                teacherId -> teacherRepository.findById(teacherId).orElse(null),
                teachingUnitId -> teachingUnitRepository.findById(teachingUnitId).orElse(null),
                roomId -> roomRepository.findById(roomId).orElse(null)
        );

        ScheduleItem updatedItem = scheduleItemRepository.save(existingItem);
        return scheduleItemMapper.toDto(updatedItem);
    };

    @Override
    public Void deleteScheduleItem(Long id) {
        ScheduleItem item = scheduleItemRepository.findById(id)
                .orElseThrow(() -> new ScheduleItemNotFoundException("Schedule item not found with id: " + id));

        scheduleItemRepository.delete(item);
        return null;
    };


}
