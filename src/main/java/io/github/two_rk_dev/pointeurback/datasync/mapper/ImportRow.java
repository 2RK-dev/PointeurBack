package io.github.two_rk_dev.pointeurback.datasync.mapper;

public record ImportRow<T>(
        T data,
        String filename,
        int rowIndex,
        String rowContext
) {
}
