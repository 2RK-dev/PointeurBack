package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.ApiKeyCreateRequest;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyResponse;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyWithRawToken;
import io.github.two_rk_dev.pointeurback.model.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApiKeyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "apiKeyHash", ignore = true)
    ApiKey fromCreateRequest(ApiKeyCreateRequest dto);

    ApiKeyWithRawToken toResponseWithRawToken(ApiKey entity, String rawToken, String prefix);

    ApiKeyResponse toResponse(ApiKey entity, String prefix);
}
