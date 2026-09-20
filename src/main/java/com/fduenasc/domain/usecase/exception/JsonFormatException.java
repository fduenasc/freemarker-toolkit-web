package com.fduenasc.domain.usecase.exception;

/**
 * Thrown when JSON cannot be formatted.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class JsonFormatException extends IllegalArgumentException {

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message detail about the error
     * @param cause   underlying exception
     */
    public JsonFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
