package io.github.two_rk_dev.pointeurback.exception;

import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableAdapter;

import java.util.Arrays;

/**
 * Unchecked exception thrown when a data import or export references an entity for what those operations are supported.
 */
public class UnknownEntityException extends RuntimeException {
    /**
     * @param entityName the name of the unknown entity
     */
    public UnknownEntityException(String entityName) {
        super("Unknown entity: " + entityName +
              ". Support entities: " + Arrays.toString(EntityTableAdapter.Type.supportedEntities()));
    }
}