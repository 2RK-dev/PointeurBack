package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record ValidationErrorDetails(
        String timestamp,
        List<FieldError> errors,
        String errorCode
) {
}

