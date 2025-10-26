package io.github.two_rk_dev.pointeurback.datasync.mapper;

import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.exception.UnknownEntityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Maps between persistence/entities and the {@link TableData} DTO used by data sync.
 * Implementations persist incoming data and retrieve stored data.
 */
public interface EntityTableAdapter {
    /**
     * Persist the provided table data.
     *
     * @param tableData the table data to persist
     */
    void persist(@NotNull TableData tableData);

    /**
     * Retrieve stored table data.
     *
     * @return the fetched {@link TableData}
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