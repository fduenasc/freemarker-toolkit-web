package com.fduenasc.application;

import com.fduenasc.domain.model.FreemarkerTemplateSyntaxCheck;
import com.fduenasc.domain.model.JsonSyntaxCheck;
import com.fduenasc.domain.usecase.exception.DataModelException;
import com.fduenasc.domain.usecase.exception.InvalidJsonException;
import com.fduenasc.domain.usecase.exception.TemplateProcessingException;
import com.fduenasc.domain.usecase.TemplateProcessor;
import com.fduenasc.domain.usecase.TemplateSyntaxChecker;
import com.fduenasc.domain.usecase.TemplateValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

/**
 * Main application service: coordinates processing, validation, and formatting.
 * 
 * @author Francisco Dueñas
 * @since 0.1.0
 */
@ApplicationScoped
public class ToolkitService {

    /**
     * The template validator.
     */
    private final TemplateValidator validator;
    /**
     * The template syntax checker.
     */
    private final TemplateSyntaxChecker syntaxChecker;

    /**
     * Constructs a new ToolkitService instance.
     *
     * @param templateProcessor the template processor.
     * @param syntaxChecker the template syntax checker.
     */
    @Inject
    public ToolkitService(TemplateProcessor templateProcessor,
                          TemplateSyntaxChecker syntaxChecker) {
        this.validator = new TemplateValidator(templateProcessor);
        this.syntaxChecker = syntaxChecker;
    }

    /**
     * Processes the template.
     *
     * @param template the template.
     * @param jsonDataModel the JSON data model.
     * @return the processed template.
     * @throws TemplateProcessingException if the template processing fails.
     * @throws DataModelException if the data model is invalid.
     */
    public String processTemplate(String template, String jsonDataModel)
            throws TemplateProcessingException, DataModelException {
        String json = jsonDataModel == null || jsonDataModel.isBlank() ? "{}" : jsonDataModel.trim();
        Map<String, Object> dataModel = TemplateValidator.parseJsonToDataModel(json);
        return validator.processTemplate(template, dataModel);
    }

    /**
     * Checks the JSON syntax.
     *
     * @param json the JSON.
     * @return the JSON syntax check.
     */
    public JsonSyntaxCheck checkJson(String json) {
        return TemplateValidator.checkDataModelJsonSyntax(json);
    }

    /**
     * Checks the template syntax.
     *
     * @param template the template.
     * @return the template syntax check.
     */
    public FreemarkerTemplateSyntaxCheck checkTemplate(String template) {
        return syntaxChecker.check(template);
    }

    /**
     * Formats the JSON.
     *
     * @param json the JSON.
     * @return the formatted JSON.
     */
    public String formatJson(String json) {
        return TemplateValidator.formatFlexibleJson(json);
    }

    /**
     * Formats the template.
     *
     * @param template the template.
     * @return the formatted template.
     */
    public String formatTemplate(String template) {
        return TemplateValidator.formatFreemarkerTemplateCombined(template);
    }

    /**
     * Converts the template to a single line.
     *
     * @param template the template.
     * @return the single line template.
     */
    public String toSingleLine(String template) {
        String singleLine = TemplateValidator.toSingleLine(template);
        return singleLine.replaceAll("}>\\s+\\{", "}>{");
    }

    /**
     * Validates the expected fields.
     *
     * @param output the output.
     * @param fields the fields.
     * @return the list of invalid fields.
     * @throws InvalidJsonException if the output is invalid.
     */
    public List<String> validateExpectedFields(String output, String[] fields) throws InvalidJsonException {
        if (output != null && output.contains("\\\"")) {
            output = output.replace("\\\"", "\"");
        }
        return TemplateValidator.validateFieldsPresentWithTypes(output, fields);
    }
}
