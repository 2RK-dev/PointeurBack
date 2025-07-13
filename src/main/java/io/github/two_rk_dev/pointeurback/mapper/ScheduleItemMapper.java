package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    @Mapping(target = "end", expression = "java(entity.getEnd().format(DATE_TIME_FORMATTER))")
    ScheduleItemDTO toDto(ScheduleItem entity);

    // Conversion CreateScheduleItemDTO -> ScheduleItem
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true) // Géré manuellement
    @Mapping(target = "teacher", ignore = true) // Géré manuellement
    @Mapping(target = "teachingUnit", ignore = true) // Géré manuellement
    @Mapping(target = "room", ignore = true) // Géré manuellement
    @Mapping(target = "start", expression = "java(parseDateTime(dto.start()))")
    @Mapping(target = "end", expression = "java(parseDateTime(dto.end()))")
    ScheduleItem fromCreateDto(CreateScheduleItemDTO dto);

    // Mise à jour depuis UpdateScheduleItemDTO
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "groups", ignore = true)
//    @Mapping(target = "teacher", ignore = true)
//    @Mapping(target = "teachingUnit", ignore = true)
//    @Mapping(target = "room", ignore = true)
//    @Mapping(target = "start", expression = "java(dto.start() != null ? parseDateTime(dto.start()) : entity.getStart())")
//    @Mapping(target = "end", expression = "java(dto.end() != null ? parseDateTime(dto.end()) : entity.getEnd())")
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateFromDto(UpdateScheduleItemDTO dto, @MappingTarget ScheduleItem entity);

    // Méthode pour parser les dates
    default LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    // Méthode utilitaire pour la création
    default ScheduleItem createFromDto(CreateScheduleItemDTO dto,
                                       Group group,
                                       Teacher teacher,
                                       TeachingUnit teachingUnit,
                                       Room room) {
        ScheduleItem item = fromCreateDto(dto);
        item.setGroups(group);
        item.setTeacher(teacher);
        item.setTeachingUnit(teachingUnit);
        item.setRoom(room);
        return item;
    }

    // Méthode utilitaire pour la mise à jour
//    default void updateScheduleItem(UpdateScheduleItemDTO updateDto,
//                                    ScheduleItem item,
//                                    Group group,
//                                    Teacher teacher,
//                                    TeachingUnit teachingUnit,
//                                    Room room) {
//        if (updateDto != null) {
//            updateFromDto(updateDto, item);
//            item.setGroups(group);
//            item.setTeacher(teacher);
//            item.setTeachingUnit(teachingUnit);
//            item.setRoom(room);
//        }
//    }
}
