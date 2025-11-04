package io.github.two_rk_dev.pointeurback.dto.datasync;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Immutable holder for table data.
 *
 * @param tableName table name
 * @param headers   column headers
 * @param rows      rows, each row is a list of column values
 */
public record TableData(
        String tableName,
        @Unmodifiable List<String> headers,
        List<@Unmodifiable List<String>> rows
) {
    public static final TableData EMPTY = new TableData("", List.of(), List.of());

    public @NotNull TableData withTableInfo(String tableName, List<String> newHeaders) {
        return new TableData(tableName, newHeaders, rows);
    }
}