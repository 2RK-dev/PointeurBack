package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractEntityTableAdapter<T extends Record> implements EntityTableAdapter {
    private final @NotNull
    @Getter Map<String, ColumnFieldBinding<T>> mapping;
    private final Class<T> dtoClass;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    protected AbstractEntityTableAdapter(ObjectMapper objectMapper, Validator validator, Class<T> dtoClass) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.mapping = Utils.buildMapping(dtoClass);
        this.dtoClass = dtoClass;
    }

    @Override
    public final @NotNull List<SyncError> process(UUID stageID, @NotNull TableData tableData, boolean ignoreConflicts) {
        List<SyncError> errors = new ArrayList<>();
        List<String> headers = tableData.headers();
        List<ImportRow<T>> toInsert = new ArrayList<>();
        for (int r = 0; r < tableData.rows().size(); r++) {
            List<String> row = tableData.rows().get(r);
            T dto;
            String context = Utils.rowToString(row, headers);
            try {
                dto = Utils.parseDTO(row, headers, dtoClass, mapping, objectMapper);
            } catch (IllegalArgumentException e) {
                errors.add(new SyncError(tableData.tableName(), r, e.getMessage(), context));
                continue;
            }
            Errors validationErrors = validator.validateObject(dto);
            if (validationErrors.hasErrors()) {
                errors.add(new SyncError(tableData.tableName(), r, formatValidationErrors(validationErrors), context));
                continue;
            }
            toInsert.add(new ImportRow<>(dto, tableData.tableName(), r, context));
        }
        stage(stageID, toInsert, ignoreConflicts);
        return errors;
    }

    /**
     * No-op, data insertion is already performed in {@link #process(UUID, TableData, boolean)} unless children override
     * this method.
     * @return an empty list
     */
    @Override
    public List<SyncError> finalize(UUID stageID, boolean ignoreConflicts) {
        return List.of();
    }

    /**
     * Stage the validated and parsed rows for the given staging session.
     * <p>
     * Implementations should persist or otherwise prepare the provided rows (each represented by an {@link ImportRow})
     * for the provided `stageID`. This method is invoked after {@link #process(UUID, TableData, boolean)} has parsed
     * and validated input rows.
     *
     * @param stageID         identifier of the current staging session
     * @param toStage         list of rows to stage; each entry contains the DTO, source table name, row index and context
     * @param ignoreConflicts if true, rows that would cause conflicts (e.g., duplicates) should be skipped, otherwise merge to existing
     */
    protected abstract void stage(UUID stageID, @NotNull List<ImportRow<T>> toStage, boolean ignoreConflicts);

    private @NotNull String formatValidationErrors(@NotNull Errors errors) {
        List<String> errorMessages = new ArrayList<>();
        errors.getFieldErrors().forEach(
                fieldError -> errorMessages.add(fieldError.getField() + ": " + fieldError.getDefaultMessage())
        );
        errors.getGlobalErrors().forEach(
                globalError -> errorMessages.add(globalError.getDefaultMessage())
        );
        return String.join("; ", errorMessages);
    }
}
