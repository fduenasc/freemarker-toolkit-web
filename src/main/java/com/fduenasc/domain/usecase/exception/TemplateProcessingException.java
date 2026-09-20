package com.fduenasc.domain.usecase.exception;

/**
 * Thrown when a FreeMarker template cannot be processed.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class TemplateProcessingException extends Exception {

    /**
     * Creates the exception with a detail message and a cause.
     *
     * @param message detail about the error
     * @param cause   underlying exception
     */
    public TemplateProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
