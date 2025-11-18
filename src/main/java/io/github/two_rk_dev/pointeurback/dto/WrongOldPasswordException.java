package io.github.two_rk_dev.pointeurback.dto;

public class WrongOldPasswordException extends RuntimeException {
    public WrongOldPasswordException(String message) {
        super(message);
    }
}
