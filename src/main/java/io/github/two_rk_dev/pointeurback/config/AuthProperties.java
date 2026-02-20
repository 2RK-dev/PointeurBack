package io.github.two_rk_dev.pointeurback.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Authentication-related configuration properties.
 *
 * <p>Bound to the {@code app.auth} prefix in {@code application.yml}.
 *
 * @param session Refresh-session and cookie-related settings (refresh lifetime, cookie flags, etc.).
 * @param jwt JWT settings (secret and access token expiration).
 * @param bootstrapSuperadmin Bootstrap superadmin account settings, used to create a superadmin on startup if none exist.
 * @param apiKey Partner API key authentication settings (header name and key prefix).
 */
@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        Session session,
        JwtProperties jwt,
        BootstrapSuperadmin bootstrapSuperadmin,
        ApiKey apiKey
) {
    /**
     * JWT configuration properties.
     *
     * @param secret Secret key used to sign JWTs. Keep this value private and provide it via environment variables or a
     *               secrets manager.
     * @param expiration Access token lifetime, in seconds.
     */
    public record JwtProperties(
            String secret,
            Long expiration
    ) {
    }

    /**
     * Bootstrap superadmin account properties.
     *
     * <p>Used on application startup to ensure a superadmin exists (for example, in a fresh database).
     *
     * @param username Username for the bootstrap superadmin account created on application startup if no superadmin exists.
     * @param password Test-only fixed superadmin password.
     *                 <p><strong>Never</strong> set this property outside test environments (or the app won't start).
     */
    public record BootstrapSuperadmin(
            String username,
            String password
    ) {
    }

    /**
     * Refresh session configuration properties.
     *
     * <p>Used for refresh token/session rotation and inactivity tracking.
     *
     * @param refreshExpiration Maximum inactivity period for a refresh session, in days.
     *                          <p>This value is used in a sliding-expiration model: each successful refresh extends the
     *                          session's expiration by this duration. Note: under this model, sessions may never expire
     *                          if they are frequently refreshed.
     * @param cookieSecure Whether authentication cookies should be marked as {@code Secure}. Disable this only in local
     *                     non-HTTPS development environments.
     */
    public record Session(
            Long refreshExpiration,
            Boolean cookieSecure
    ) {
    }

    /**
     * Partner API key authentication properties.
     *
     * <p>These settings apply to machine-to-machine calls from trusted partner services inside the ecosystem.
     *
     * @param header HTTP header name from which the API key is extracted (for example {@code X-Api-Key}).
     * @param keyPrefix Prefix expected at the beginning of issued API keys (for example {@code ptr_}). This helps with
     *                  key identification and allows fast rejection of obviously invalid keys before any lookup/hash work.
     */
    public record ApiKey(
            String header,
            String keyPrefix
    ) {
    }
}
