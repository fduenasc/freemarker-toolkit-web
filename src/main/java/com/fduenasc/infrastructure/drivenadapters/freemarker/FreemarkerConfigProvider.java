package com.fduenasc.infrastructure.drivenadapters.freemarker;

import com.fduenasc.infrastructure.helpers.UserPreferences;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides a Freemarker configuration with user preferences.
 * 
 * @author Francisco Dueñas
 * @since 0.1.0
 */
@ApplicationScoped
public class FreemarkerConfigProvider {

    /**
     * The user preferences.
     */
    private final UserPreferences preferences;

    /**
     * Constructs a new FreemarkerConfigProvider instance.
     * 
     * @param preferences the user preferences.
     */
    @Inject
    public FreemarkerConfigProvider(UserPreferences preferences) {
        this.preferences = preferences;
    }
    
    /**
     * Creates a new Freemarker configuration.
     * 
     * @return the new Freemarker configuration.
     */
    public Configuration createConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_34);
        configuration.setDefaultEncoding("UTF-8");

        String localeStr = preferences.getLocale();
        String timeZoneStr = preferences.getTimeZone();

        if (localeStr != null && !localeStr.isEmpty()) {
            String[] parts = localeStr.split("_");
            if (parts.length == 2) {
                configuration.setLocale(Locale.of(parts[0], parts[1]));
            } else {
                configuration.setLocale(Locale.forLanguageTag(localeStr));
            }
        }

        if (timeZoneStr != null && !timeZoneStr.isEmpty()) {
            configuration.setTimeZone(TimeZone.getTimeZone(timeZoneStr));
        }

        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

        return configuration;
    }
}
