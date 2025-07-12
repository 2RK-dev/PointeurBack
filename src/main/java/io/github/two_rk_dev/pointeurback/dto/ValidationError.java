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
        return new ValidationError(((FieldError) e).getField(), e.getDefaultMessage());
    }
}
