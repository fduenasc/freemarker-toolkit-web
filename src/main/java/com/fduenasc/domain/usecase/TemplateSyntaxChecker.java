package com.fduenasc.domain.usecase;

import com.fduenasc.domain.model.FreemarkerTemplateSyntaxCheck;

/**
 * Contract for checking whether a FreeMarker template is well-formed (without using data).
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public interface TemplateSyntaxChecker {

    /**
     * Checks whether a FreeMarker template is well-formed.
     *
     * @param source the FreeMarker template content.
     * @return the check result.
     */
    FreemarkerTemplateSyntaxCheck check(String source);
}
