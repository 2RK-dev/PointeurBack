package io.github.two_rk_dev.pointeurback.dto;

import java.time.OffsetDateTime;

public record ErrorDetails(
        OffsetDateTime timestamp,
        String message,
        String details,
        String errorCode
) {
}
