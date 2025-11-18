package io.github.two_rk_dev.pointeurback.dto;

public record UserCreatedDTO(
        Long id,
        String password,
        UserInfoDTO info
) {
}
