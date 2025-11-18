package io.github.two_rk_dev.pointeurback.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateUserDTO(
        @NotNull
        @Pattern(regexp = "^[a-zA-Z0-9_\\-]{1,50}$", message = "Username should only consist of latin characters, dash(-), underscore (_) and numbers")
        String username
) {
}
