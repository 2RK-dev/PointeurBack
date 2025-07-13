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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Mapper(componentModel = "spring",
        uses = {TeacherMapper.class, TeachingUnitMapper.class, RoomMapper.class, GroupMapper.class})
public interface ScheduleItemMapper {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Conversion de base ScheduleItem -> ScheduleItemDTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "groups", source = "groups") // Conversion automatique via GroupMapper
    @Mapping(target = "teacher", source = "teacher")
    @Mapping(target = "teachingUnit", source = "teachingUnit")
    @Mapping(target = "room", source = "room")
    @Mapping(target = "start", expression = "java(entity.getStart().format(DATE_TIME_FORMATTER))")
    @Mapping(target = "endTime", expression = "java(entity.getEndTime().format(DATE_TIME_FORMATTER))")
    ScheduleItemDTO toDto(ScheduleItem entity);

    List<ScheduleItemDTO> toDtoList(List<ScheduleItem> entity);

    // Conversion CreateScheduleItemDTO -> ScheduleItem
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true) // Géré manuellement
    @Mapping(target = "teacher", ignore = true) // Géré manuellement
    @Mapping(target = "teachingUnit", ignore = true) // Géré manuellement
    @Mapping(target = "room", ignore = true) // Géré manuellement
    @Mapping(target = "start", expression = "java(parseDateTime(dto.start()))")
    @Mapping(target = "endTime", expression = "java(parseDateTime(dto.endTime()))")
    ScheduleItem fromCreateDto(CreateScheduleItemDTO dto);

    // Mise à jour depuis UpdateScheduleItemDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true) // Même approche que dans fromCreateDto
    @Mapping(target = "teacher", ignore = true) // Même approche que dans fromCreateDto
    @Mapping(target = "teachingUnit", ignore = true) // Même approche que dans fromCreateDto
    @Mapping(target = "room", ignore = true) // Même approche que dans fromCreateDto
    @Mapping(target = "start",
            expression = "java(dto.start() != null ? parseDateTime(dto.start()) : entity.getStart())")
    @Mapping(target = "endTime", // Garde la même nomenclature que toDto
            expression = "java(dto.endTime() != null ? parseDateTime(dto.endTime()) : entity.getEndTime())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateScheduleItemDTO dto, @MappingTarget ScheduleItem entity);

    // Méthode pour parser les dates
    default LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    default ScheduleItem createFromDto(@NotNull CreateScheduleItemDTO dto,
                                       @NotNull Function<Long, Group> groupProvider,
                                       @NotNull Function<Long, Teacher> teacherProvider,
                                       @NotNull Function<Long, TeachingUnit> teachingUnitProvider,
                                       @NotNull Function<Long, Room> roomProvider) {
        // Validation des paramètres
        Objects.requireNonNull(dto, "CreateScheduleItemDTO cannot be null");
        Objects.requireNonNull(groupProvider, "Group provider cannot be null");
        Objects.requireNonNull(teacherProvider, "Teacher provider cannot be null");
        Objects.requireNonNull(teachingUnitProvider, "TeachingUnit provider cannot be null");
        Objects.requireNonNull(roomProvider, "Room provider cannot be null");

        // Résolution des entités associées
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

        Room room = roomProvider.apply(dto.roomId());
        if (room == null) {
            throw new RoomNotFoundException("Room not found with id: " + dto.roomId());
        }

        // Création de l'entité ScheduleItem
        ScheduleItem item = fromCreateDto(dto);
        item.setGroups(groups); // Adaptation à votre modèle actuel (ManyToOne)
        item.setTeacher(teacher);
        item.setTeachingUnit(teachingUnit);
        item.setRoom(room);

        // Validation des contraintes métier
        if (item.getStart() == null || item.getEndTime() == null) {
            throw new IllegalStateException("Start and end times must be specified");
        }
        if (item.getEndTime().isBefore(item.getStart())) {
            throw new IllegalStateException("End time cannot be before start time");
        }

        return item;
    }

    // Méthode utilitaire pour la mise à jour
    default void updateFromDto(@NotNull UpdateScheduleItemDTO dto,
                               @MappingTarget ScheduleItem entity,
                               @NotNull Function<List<Long>, List<Group>> groupProvider,
                               @NotNull Function<Long, Teacher> teacherProvider,
                               @NotNull Function<Long, TeachingUnit> teachingUnitProvider,
                               @NotNull Function<Long, Room> roomProvider) {

        // 1. Validation des paramètres
        Objects.requireNonNull(dto, "UpdateScheduleItemDTO cannot be null");
        Objects.requireNonNull(entity, "Entity cannot be null");

        // 2. Mise à jour des champs simples
        if (dto.start() != null) {
            entity.setStart(parseDateTime(dto.start()));
        }
        if (dto.endTime() != null) {
            entity.setEndTime(parseDateTime(dto.endTime())); // Cohérent avec votre entité
        }

        // 3. Mise à jour des relations (uniquement si fournies dans le DTO)
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
        }

        // 4. Validation métier après mise à jour
        if (entity.getEndTime().isBefore(entity.getStart())) {
            throw new IllegalStateException("End time cannot be before start time");
        }
    }
}
