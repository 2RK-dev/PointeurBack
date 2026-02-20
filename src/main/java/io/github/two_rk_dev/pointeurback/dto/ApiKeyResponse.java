package io.github.two_rk_dev.pointeurback.dto;

import java.time.Instant;

public record ApiKeyResponse(
        Long id,
        String name,
        String prefix,
        Instant createdAt
) {
}
