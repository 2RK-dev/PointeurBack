package io.github.two_rk_dev.pointeurback.dto.datasync;

import java.util.List;
import java.util.Map;

public record ImportResponse(
        int totalRows,
        int successfulRows,
        int failedRows,
        List<SyncError> errors,
        Map<String, Integer> entitySummary
) {
    public static ImportResponse withErrors(int total, int success, List<SyncError> errors, Map<String, Integer> summary) {
        return new ImportResponse(total, success, total - success, errors, summary);
    }

    public static ImportResponse successful(int total, Map<String, Integer> summary) {
        return new ImportResponse(total, total, 0, List.of(), summary);
    }
}