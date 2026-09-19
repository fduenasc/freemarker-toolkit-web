package com.fduenasc.domain.usecase.exception;

/**
 * Thrown when the JSON data model cannot be read correctly.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class DataModelException extends Exception {

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message detail about the error
     * @param cause   underlying exception
     */
    public DataModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
