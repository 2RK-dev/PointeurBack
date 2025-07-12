package io.github.two_rk_dev.pointeurback.dto;

public record ErrorDetails(
        String timestamp,
        String message,
        String errorCode
) {
}
