package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.RefreshTokenDTO;
import io.github.two_rk_dev.pointeurback.model.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "maxAge", source = "maxAge")
    RefreshTokenDTO toDto(RefreshToken refreshToken, Duration maxAge);
}
