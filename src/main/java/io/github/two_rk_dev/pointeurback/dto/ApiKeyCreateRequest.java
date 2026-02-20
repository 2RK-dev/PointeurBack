package io.github.two_rk_dev.pointeurback.dto;

import org.hibernate.validator.constraints.Length;

public record ApiKeyCreateRequest(
        @Length(min = 3, max = 50) String name
) {
}
