package com.fduenasc.domain.model;

/**
 * Result of checking the JSON data model syntax.
 *
 * @param syntaxValid whether the JSON is syntactically valid.
 * @param message     empty when OK; parse error text or a stable message key.
 * @param line        1-based line, or {@code -1} if unknown.
 * @param column      1-based column, or {@code -1} if unknown.
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public record JsonSyntaxCheck(boolean syntaxValid, String message, int line, int column) {
}
