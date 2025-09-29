package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.exception.RoomNotFoundException;
import io.github.two_rk_dev.pointeurback.exception.TeacherNotFoundException;
import io.github.two_rk_dev.pointeurback.exception.TeachingUnitNotFoundException;
import io.github.two_rk_dev.pointeurback.model.*;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Mapper(componentModel = "spring",
        uses = {TeacherMapper.class, TeachingUnitMapper.class, RoomMapper.class, GroupMapper.class})
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "teachingUnit", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "startTime", expression = "java(parseDateTime(dto.startTime()))")
    @Mapping(target = "endTime", expression = "java(parseDateTime(dto.endTime()))")
    ScheduleItem fromCreateDto(CreateScheduleItemDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "teachingUnit", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "startTime",
            expression = "java(dto.startTime() != null ? parseDateTime(dto.startTime()) : entity.getStartTime())")
    @Mapping(target = "endTime",
            expression = "java(dto.endTime() != null ? parseDateTime(dto.endTime()) : entity.getEndTime())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateScheduleItemDTO dto, @MappingTarget ScheduleItem entity);

    default OffsetDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        return OffsetDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    default ScheduleItem createFromDto(@NotNull CreateScheduleItemDTO dto,
                                       @NotNull Function<Long, Group> groupProvider,
                                       @NotNull Function<Long, Teacher> teacherProvider,
                                       @NotNull Function<Long, TeachingUnit> teachingUnitProvider,
                                       @NotNull Function<Long, Room> roomProvider) {

        List<Group> groups = dto.groupIds().stream()
                .map(groupId -> {
                    Group group = groupProvider.apply(groupId);
                    if (group == null) {
                        throw new GroupNotFoundException("Group not found with id: " + groupId);
                    }
                    return group;
                })
                .toList();

        Teacher teacher = teacherProvider.apply(dto.teacherId());
        if (teacher == null) {
            throw new TeacherNotFoundException("Teacher not found with id: " + dto.teacherId());
        }

        TeachingUnit teachingUnit = teachingUnitProvider.apply(dto.teachingUnitId());
        if (teachingUnit == null) {
            throw new TeachingUnitNotFoundException("TeachingUnit not found with id: " + dto.teachingUnitId());
        }

        Room room = null;
        if (dto.roomId() != null) {
            room = roomProvider.apply(dto.roomId());
            if (room == null) {
                throw new RoomNotFoundException("Room not found with id: " + dto.roomId());
            }
        }

        ScheduleItem item = fromCreateDto(dto);
        item.setGroups(groups);
        item.setTeacher(teacher);
        item.setTeachingUnit(teachingUnit);
        item.setRoom(room);

        if (item.getStartTime() == null || item.getEndTime() == null) {
            throw new IllegalStateException("Start and end times must be specified");
        }
        if (item.getEndTime().isBefore(item.getStartTime())) {
            throw new IllegalStateException("End time cannot be before startTime time");
        }

        return item;
    }

    default void updateFromDto(@NotNull UpdateScheduleItemDTO dto,
                               @MappingTarget ScheduleItem entity,
                               @NotNull Function<List<Long>, List<Group>> groupProvider,
                               @NotNull Function<Long, Teacher> teacherProvider,
                               @NotNull Function<Long, TeachingUnit> teachingUnitProvider,
                               @NotNull Function<Long, Room> roomProvider) {

        Objects.requireNonNull(entity, "Entity cannot be null");


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
            Teacher teacher = teacherProvider.apply(dto.teacherId());
            if (teacher == null) {
                throw new TeacherNotFoundException("Teacher not found with id: " + dto.teacherId());
            }
            entity.setTeacher(teacher);
        }

        if (dto.teachingUnitId() != null) {
            TeachingUnit teachingUnit = teachingUnitProvider.apply(dto.teachingUnitId());
            if (teachingUnit == null) {
                throw new TeachingUnitNotFoundException("TeachingUnit not found with id: " + dto.teachingUnitId());
            }
            entity.setTeachingUnit(teachingUnit);
        }

        if (dto.roomId() != null) {
            Room room = roomProvider.apply(dto.roomId());
            if (room == null) {
                throw new RoomNotFoundException("Room not found with id: " + dto.roomId());
            }
            entity.setRoom(room);
        } else entity.setRoom(null);

        if (entity.getEndTime().isBefore(entity.getStartTime())) {
            throw new IllegalStateException("End time cannot be before startTime time");
        }
    }
}
