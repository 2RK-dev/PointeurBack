package io.github.two_rk_dev.pointeurback.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        Long refreshSessionExpiration,
        JwtProperties jwt
) {
    public record JwtProperties(
            String secret,
            Long expiration
    ) {
    }
}
