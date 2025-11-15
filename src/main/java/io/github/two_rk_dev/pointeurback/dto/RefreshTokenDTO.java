package io.github.two_rk_dev.pointeurback.dto;

import java.time.Duration;

public record RefreshTokenDTO(
        String token,
        Duration maxAge,
        String deviceId
) {
}
