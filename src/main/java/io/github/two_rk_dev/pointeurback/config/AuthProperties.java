package io.github.two_rk_dev.pointeurback.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param refreshSessionExpiration Maximum inactivity period for a refresh session, in days. This value is used in a
 *                                 sliding-expiration model: each successful refresh extends the session's expiration by
 *                                 this duration. Note: under this model, sessions may never expire if they are
 *                                 frequently refreshed.
 * @param cookieSecure Flag indicating whether authentication cookies should be marked as secure. This flag is so it can
 *                    be disabled in non-HTTPS development environments.
 * @param jwt
 * @param bootstrapSuperadmin
 */
@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        Long refreshSessionExpiration,
        Boolean cookieSecure,
        JwtProperties jwt,
        BootstrapSuperadmin bootstrapSuperadmin
) {
    /**
     * @param secret Secret key of JWT.
     * @param expiration Expiration of the JWT, in seconds.
     */
    public record JwtProperties(
            String secret,
            Long expiration
    ) {
    }

    /**
     * @param username Username for the bootstrap superadmin account created on application startup if no superadmin exist.
     * @param password Test-only fixed superadmin password. Never set this property in environments where the test profile
     *                 is not active, or the app won't start.
     */
    public record BootstrapSuperadmin(
            String username,
            String password
    ) {
    }
}
