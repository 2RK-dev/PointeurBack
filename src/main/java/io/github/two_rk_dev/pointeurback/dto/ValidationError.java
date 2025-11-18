package io.github.two_rk_dev.pointeurback.dto;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public record ValidationError(
        String field,
        String error
) {
    @Contract("_ -> new")
    public static @NotNull ValidationError fromFieldError(ObjectError e) {
        if (e instanceof FieldError fe) {
            return new ValidationError(fe.getField(), fe.getDefaultMessage());
        } else return new ValidationError(e.getObjectName(), e.getDefaultMessage());
    }
}
