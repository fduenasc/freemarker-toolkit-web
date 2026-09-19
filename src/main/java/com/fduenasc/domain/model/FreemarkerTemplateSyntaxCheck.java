package com.fduenasc.domain.model;

/**
 * Result of checking FreeMarker template syntax (no data model).
 *
 * @param syntaxValid whether the template parses successfully.
 * @param message     empty when OK; otherwise parser error text.
 * @param line        1-based line, or {@code -1} if unknown.
 * @param column      1-based column, or {@code -1} if unknown.
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public record FreemarkerTemplateSyntaxCheck(boolean syntaxValid, String message, int line, int column) {
}
