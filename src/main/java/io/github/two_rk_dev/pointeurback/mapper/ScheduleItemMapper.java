package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.exception.*;
import io.github.two_rk_dev.pointeurback.model.*;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mapper(componentModel = "spring", uses = {TeacherMapper.class, TeachingUnitMapper.class, RoomMapper.class,
        GroupMapper.class})
public interface ScheduleItemMapper {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "groups", source = "groups")
    @Mapping(target = "teacher", source = "teacher")
    @Mapping(target = "teachingUnit", source = "teachingUnit")
    @Mapping(target = "room", source = "room")
    @Mapping(target = "startTime", expression = "java(entity.getStartTime().format(DATE_TIME_FORMATTER))")
    @Mapping(target = "endTime", expression = "java(entity.getEndTime().format(DATE_TIME_FORMATTER))")
    ScheduleItemDTO toDto(ScheduleItem entity);

    List<ScheduleItemDTO> toDtoList(List<ScheduleItem> entity);

    default OffsetDateTime parseDateTime(String dateTimeStr) {
        return OffsetDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    default ScheduleItem createFromDto(@NotNull CreateScheduleItemDTO dto,
                                       @NotNull Function<Long, Optional<Group>> groupProvider,
                                       @NotNull Function<Long, Optional<Teacher>> teacherProvider,
                                       @NotNull Function<Long, Optional<TeachingUnit>> teachingUnitProvider,
                                       @NotNull Function<Long, Optional<Room>> roomProvider) {

        List<Group> groups = dto.groupIds().stream()
                .map(groupId -> groupProvider.apply(groupId)
                        .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId))
                )
                .toList();

        Teacher teacher = teacherProvider.apply(dto.teacherId())
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + dto.teacherId()));

        TeachingUnit teachingUnit = teachingUnitProvider.apply(dto.teachingUnitId())
                .orElseThrow(() -> new TeachingUnitNotFoundException("TeachingUnit not found with id: " + dto.teachingUnitId()));
        Room room = null;
        if (dto.roomId() != null) {
            room = roomProvider.apply(dto.roomId())
                    .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + dto.roomId()));
        }

        ScheduleItem item = new ScheduleItem();
        item.setGroups(groups);
        item.setTeacher(teacher);
        item.setTeachingUnit(teachingUnit);
        item.setRoom(room);
        item.setStartTime(parseDateTime(dto.startTime()));
        item.setEndTime(parseDateTime(dto.endTime()));

        if (item.getEndTime().isBefore(item.getStartTime())) {
            throw new InvalidDateRangeException("End time cannot be before startTime time");
        }
        return item;
    }

    default void updateFromDto(@NotNull UpdateScheduleItemDTO dto,
                               @MappingTarget ScheduleItem entity,
                               @NotNull Function<List<Long>, List<Group>> groupProvider,
                               @NotNull Function<Long, Optional<Teacher>> teacherProvider,
                               @NotNull Function<Long, Optional<TeachingUnit>> teachingUnitProvider,
                               @NotNull Function<Long, Optional<Room>> roomProvider) {

        if (dto.startTime() != null) {
            entity.setStartTime(parseDateTime(dto.startTime()));
        }
        if (dto.endTime() != null) {
            entity.setEndTime(parseDateTime(dto.endTime()));
        }

        if (dto.groupIds() != null) {
            List<Group> groups = groupProvider.apply(dto.groupIds());
            if (groups.size() != dto.groupIds().size()) {
                throw new GroupNotFoundException("Certains groupes n'ont pas été trouvés");
            }
            entity.setGroups(groups);
        }

        if (dto.teacherId() != null) {
            Teacher teacher = teacherProvider.apply(dto.teacherId())
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + dto.teacherId()));
            entity.setTeacher(teacher);
        }

        if (dto.teachingUnitId() != null) {
            TeachingUnit teachingUnit = teachingUnitProvider.apply(dto.teachingUnitId())
                    .orElseThrow(() -> new TeachingUnitNotFoundException("TeachingUnit not found with id: " + dto.teachingUnitId()));
            entity.setTeachingUnit(teachingUnit);
        }

        if (dto.roomId() != null) {
            Room room = roomProvider.apply(dto.roomId())
                    .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + dto.roomId()));
            entity.setRoom(room);
        } else
            entity.setRoom(null);

        if (entity.getEndTime().isBefore(entity.getStartTime())) {
            throw new InvalidDateRangeException("End time cannot be before startTime time");
        }
    }
}
