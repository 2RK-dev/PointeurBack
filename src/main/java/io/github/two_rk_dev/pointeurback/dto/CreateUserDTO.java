package io.github.two_rk_dev.pointeurback.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateUserDTO(
        @NotNull
        @Pattern(regexp = "^\\w{1,50}$", message = "Username should only consist of latin characters, underscore and numbers")
        String username
) {
}
