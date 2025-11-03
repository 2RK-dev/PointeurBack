package io.github.two_rk_dev.pointeurback.dto.datasync;

import java.util.Map;

public record ImportMapping(
        /// The keys of the outer map are the file names, those of the inner map are the "subfiles'" name.
        Map<String, Map<String, TableMapping>> metadata
) {
}
