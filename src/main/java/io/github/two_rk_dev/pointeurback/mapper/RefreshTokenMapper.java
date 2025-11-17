package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.RefreshTokenDTO;
import io.github.two_rk_dev.pointeurback.model.RefreshToken;
import org.mapstruct.Mapper;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    RefreshTokenDTO toDto(RefreshToken refreshToken, Duration maxAge, boolean secure);
}
