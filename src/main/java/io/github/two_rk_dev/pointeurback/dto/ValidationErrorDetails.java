package io.github.two_rk_dev.pointeurback.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ValidationErrorDetails(
        OffsetDateTime timestamp,
        List<ValidationError> errors,
        String errorCode
) {
}

