package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateUserDTO;
import io.github.two_rk_dev.pointeurback.dto.UserCreatedDTO;
import io.github.two_rk_dev.pointeurback.dto.UserDTO;
import io.github.two_rk_dev.pointeurback.dto.UserInfoDTO;
import io.github.two_rk_dev.pointeurback.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @MappingQualifier.UserToUserInfoDTO
    UserInfoDTO toInfoDto(User user);

    @Mapping(target = "info", source = "user", qualifiedBy = MappingQualifier.UserToUserInfoDTO.class)
    UserDTO toDto(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    User fromCreateDTO(CreateUserDTO dto);

    @Mapping(target = "info", source = "user", qualifiedBy = MappingQualifier.UserToUserInfoDTO.class)
    @Mapping(target = "password", source = "password")
    UserCreatedDTO toCreatedDTO(User user, String password);
}
