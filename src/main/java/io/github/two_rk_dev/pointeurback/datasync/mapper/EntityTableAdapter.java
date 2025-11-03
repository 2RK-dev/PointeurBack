package io.github.two_rk_dev.pointeurback.datasync.mapper;

import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.exception.UnknownEntityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Maps between persistence/entities and the {@link TableData} DTO used by data sync.
 * Implementations handle import and export of entity data in tabular format.
 * <p>
 * This adapter supports a two-phase import workflow to handle cross-file foreign key dependencies:
 * <ol>
 *   <li>{@link #process(UUID, TableData)} - validates and persists data</li>
 *   <li>{@link #finalize(UUID)} - resolves foreign keys and finalizes import</li>
 * </ol>
 * <p>
 * For entities without foreign key dependencies (e.g., Level, Teacher, Room), {@code process()} persists directly to
 * final tables for performance. For entities with dependencies (e.g., Group, ScheduleItem), {@code process()}
 * stages data temporarily, and {@code finalize()} promotes it after foreign key resolution.
 */
public interface EntityTableAdapter {
    /**
     * Processes and persists the provided table data for a given import session.
     * <p>
     * This method parses raw table data into DTOs, validates them using Jakarta Validation, and persists valid entities.
     * <p>
     * <strong>Behavior varies by entity type:</strong>
     * <ul>
     *   <li><strong>Entities without FK dependencies:</strong> Persists directly to final tables</li>
     *   <li><strong>Entities with FK dependencies:</strong> Stages to temporary tables with row metadata
     *       (row index and stringified context) for error reporting during {@link #finalize(UUID)}</li>
     * </ul>
     *
     * @param stageID   unique identifier for this import session, used to group related data
     * @param tableData the tabular data to process, containing headers and rows
     * @return list of validation errors encountered during parsing and validation (empty if all rows were valid)
     */
    List<SyncError> process(UUID stageID, @NotNull TableData tableData);

    /**
     * Finalizes the import session by promoting staged data to final tables.
     * <p>
     * This method validates foreign key constraints and promotes staged entities in dependency order (parents before
     * children).
     * <p>
     * For entities that were persisted directly during {@link #process(UUID, TableData)}
     * (i.e., entities without FK dependencies), this method is a no-op and returns an empty list.
     * <p>
     * This should be called after all related data has been processed via {@code process()}.
     *
     * @param stageID unique identifier for the import session to finalize
     * @return list of foreign key validation errors discovered during promotion (empty if all references were valid)
     */
    List<SyncError> finalize(UUID stageID);

    /**
     * Retrieves stored entity data in tabular format for export.
     *
     * @return the entity data as {@link TableData}, including headers and rows, never null
     */
    @NotNull TableData fetch();

    @Getter
    @AllArgsConstructor
    @Accessors(fluent = true)
    enum Type {
        ROOM("room"),
        TEACHER("teacher"),
        TEACHING_UNIT("teaching_unit"),
        GROUP("group"),
        LEVEL("level");

        private final String entityName;

        @Contract(pure = true)
        public @NotNull String beanName() {
            return entityName + "_table_adapter";
        }

        public static String @NotNull [] supportedEntities() {
            return Arrays.stream(values()).map(Type::entityName).toArray(String[]::new);
        }

        public static Type forEntity(String entityName) {
            return Arrays.stream(values())
                    .filter(type -> type.entityName.equals(entityName))
                    .findFirst()
                    .orElseThrow(() -> new UnknownEntityException(entityName));
        }
    }
}