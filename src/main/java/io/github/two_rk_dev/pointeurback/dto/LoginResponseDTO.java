package io.github.two_rk_dev.pointeurback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDTO(
        @JsonProperty("access_token") String accessToken,
        UserDTO user
) {
}
