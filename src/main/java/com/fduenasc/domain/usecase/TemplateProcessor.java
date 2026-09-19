package com.fduenasc.domain.usecase;

import com.fduenasc.domain.usecase.exception.TemplateProcessingException;

import java.util.Map;

/**
 * Contract for processing a FreeMarker template with its data.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public interface TemplateProcessor {

    /**
     * Processes a FreeMarker template with the given data model.
     *
     * @param templateContent The content of the FreeMarker template.
     * @param dataModel       The data model to use for the template.
     * @return The processed template.
     * @throws TemplateProcessingException if the template cannot be processed.
     */
    String processTemplate(String templateContent, Map<String, Object> dataModel) throws TemplateProcessingException;
}
