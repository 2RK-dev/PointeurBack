package io.github.two_rk_dev.pointeurback.dto.datasync;

import java.util.Map;

public record TableMapping(
        String entityType,
        /// Maps headers: the keys are the "headers" inside the file and the values are the property names of the target entity
        Map<String, String> headersMapping
) {
}
