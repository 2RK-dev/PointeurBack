package io.github.two_rk_dev.pointeurback.dto.datasync;

public record SyncError(
        String entityType,
        int rowIndex,
        String errorMessage,
        String invalidValue
) {
    public static SyncError forEntity(String entityType, int rowIndex, String error, String value) {
        return new SyncError(entityType, rowIndex, error, value);
    }
}
