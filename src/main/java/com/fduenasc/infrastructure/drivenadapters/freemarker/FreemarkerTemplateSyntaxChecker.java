package com.fduenasc.infrastructure.drivenadapters.freemarker;

import com.fduenasc.domain.model.FreemarkerTemplateSyntaxCheck;
import com.fduenasc.domain.usecase.TemplateSyntaxChecker;
import freemarker.core.ParseException;
import freemarker.template.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.StringReader;

/**
 * Checks the syntax of Freemarker templates.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
@ApplicationScoped
public class FreemarkerTemplateSyntaxChecker implements TemplateSyntaxChecker {

    /**
     * The Freemarker configuration provider.
     */
    private final FreemarkerConfigProvider configProvider;

    /**
     * Constructs a new FreemarkerTemplateSyntaxChecker instance.
     *
     * @param configProvider the Freemarker configuration provider.
     */
    @Inject
    public FreemarkerTemplateSyntaxChecker(FreemarkerConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    /**
     * Checks the syntax of a Freemarker template.
     *
     * @param source the template source.
     * @return the syntax check result.
     */
    @Override
    public FreemarkerTemplateSyntaxCheck check(String source) {
        String t = source == null ? "" : source;
        if (t.isEmpty()) {
            return new FreemarkerTemplateSyntaxCheck(true, "", -1, -1);
        }
        try {
            new Template("syntax-check", new StringReader(t), configProvider.createConfiguration());
            return new FreemarkerTemplateSyntaxCheck(true, "", -1, -1);
        } catch (ParseException e) {
            int line = e.getLineNumber();
            int col = e.getColumnNumber();
            if (line < 1) {
                line = -1;
            }
            if (col < 1) {
                col = -1;
            }
            String msg = e.getMessage() != null ? e.getMessage() : "Parse error";
            return new FreemarkerTemplateSyntaxCheck(false, msg, line, col);
        } catch (IOException e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return new FreemarkerTemplateSyntaxCheck(false, msg, -1, -1);
        }
    }
}
