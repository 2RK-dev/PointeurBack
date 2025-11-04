package io.github.two_rk_dev.pointeurback.dto.datasync;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class ImportSummary {
    private int totalRows = 0;
    private int successfulRows = 0;
    private int failedRows = 0;
    private final List<SyncError> errors = new ArrayList<>();
    private final List<String> skippedFiles = new ArrayList<>();
    private final Map<String, Integer> entitySummary = new HashMap<>();

    public void updateSuccessfulRows(@NotNull Function<Integer, Integer> successfulRowsUpdater) {
        this.successfulRows = successfulRowsUpdater.apply(successfulRows);
        this.failedRows = this.totalRows - this.successfulRows;
    }

    public void updateTotalRows(@NotNull Function<Integer, Integer> totalRowsUpdater) {
        this.totalRows = totalRowsUpdater.apply(totalRows);
    }
}