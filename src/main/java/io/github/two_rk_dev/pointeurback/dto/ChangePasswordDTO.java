package io.github.two_rk_dev.pointeurback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.two_rk_dev.pointeurback.validation.FieldMatch;
import jakarta.validation.constraints.NotBlank;

@FieldMatch(first = "new_", second = "confirm", message = "Password confirmation must match")
public record ChangePasswordDTO(
        @NotBlank String old,
        @JsonProperty("new") @NotBlank String new_,
        @NotBlank String confirm
) {
}
