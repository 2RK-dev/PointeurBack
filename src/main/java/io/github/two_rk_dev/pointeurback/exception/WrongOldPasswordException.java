package io.github.two_rk_dev.pointeurback.exception;

public class WrongOldPasswordException extends RuntimeException {
    public WrongOldPasswordException(String message) {
        super(message);
    }
}
