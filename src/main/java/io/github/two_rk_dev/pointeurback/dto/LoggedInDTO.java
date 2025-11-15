package io.github.two_rk_dev.pointeurback.dto;

public record LoggedInDTO(
        RefreshTokenDTO refreshToken,
        LoginResponseDTO responseDTO
) {
}
