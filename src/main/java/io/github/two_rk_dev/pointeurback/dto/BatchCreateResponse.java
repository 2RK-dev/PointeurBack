package io.github.two_rk_dev.pointeurback.dto;

import java.util.List;

public record BatchCreateResponse<SUCCESS_TYPE extends Record, FAILURE_TYPE extends Record>(
        List<SUCCESS_TYPE> successItems,
        List<FailedItem<FAILURE_TYPE>> failedItems
) {
    public record FailedItem<T>(
            T item,
            String reason
    ) {
    }
}
