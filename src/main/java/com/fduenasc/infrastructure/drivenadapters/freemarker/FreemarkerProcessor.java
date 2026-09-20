package com.fduenasc.infrastructure.drivenadapters.freemarker;

import com.fduenasc.domain.usecase.exception.TemplateProcessingException;
import com.fduenasc.domain.usecase.TemplateProcessor;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * Processes templates using Freemarker.
 */
@ApplicationScoped
public class FreemarkerProcessor implements TemplateProcessor {

    /**
     * The Freemarker configuration provider.
     */
    private final FreemarkerConfigProvider configProvider;

    /**
     * Constructs a new FreemarkerProcessor instance.
     *
     * @param freemarkerConfigProvider the Freemarker configuration provider.
     */
    @Inject
    public FreemarkerProcessor(FreemarkerConfigProvider freemarkerConfigProvider) {
        this.configProvider = freemarkerConfigProvider;
    }

    /**
     * Processes a template.
     *
     * @param templateContent the template content.
     * @param dataModel       the data model.
     * @return the processed template.
     * @throws TemplateProcessingException if the template processing fails.
     */
    @Override
    public String processTemplate(String templateContent, Map<String, Object> dataModel) throws TemplateProcessingException {
        try {
            Template template = new Template("template", new StringReader(templateContent), configProvider.createConfiguration());
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (TemplateException | IOException e) {
            throw new TemplateProcessingException(e.getMessage(), e);
        }
    }
}
