package io.github.two_rk_dev.pointeurback.datasync.mapper;

import java.util.function.Function;

public record ColumnFieldBinding<T>(
        /// Column index
        int order,
        Function<T, String> reader,
        Class<?> fieldType
) {
}
