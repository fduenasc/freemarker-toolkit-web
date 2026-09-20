package com.fduenasc.domain.usecase.exception;

/**
 * Thrown when JSON is invalid while validating fields or showing results.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class InvalidJsonException extends Exception {

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message detail about the error
     * @param cause   underlying exception
     */
    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
