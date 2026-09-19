package com.fduenasc.infrastructure.helpers;

import jakarta.enterprise.context.SessionScoped;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents user preferences for the application.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
@SessionScoped
public class UserPreferences implements Serializable {

    /**
     * The default locale.
     */
    private static final String DEFAULT_LOCALE = "en_US";

    /**
     * The default time zone.
     */
    private static final String DEFAULT_TIME_ZONE = "UTC";

    /**
     * The default UI language.
     */
    private static final String DEFAULT_UI_LANGUAGE = "es";

    /**
     * The locale.
     */
    private String locale = DEFAULT_LOCALE;

    /**
     * The time zone.
     */
    private String timeZone = DEFAULT_TIME_ZONE;
    private String uiLanguage = DEFAULT_UI_LANGUAGE;

    /**
     * Whether expected fields are visible.
     */
    private boolean expectedFieldsVisible = true;

    /**
     * The expected field entries.
     */
    private final List<String> expectedFieldEntries = new ArrayList<>();

    /**
     * Gets the locale.
     *
     * @return the locale.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale the locale to set.
     */
    public void setLocale(String locale) {
        if (locale != null && !locale.isBlank()) {
            this.locale = locale;
        }
    }

    /**
     * Gets the time zone.
     *
     * @return the time zone.
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the time zone.
     *
     * @param timeZone the time zone to set.
     */
    public void setTimeZone(String timeZone) {
        if (timeZone != null && !timeZone.isBlank()) {
            this.timeZone = timeZone;
        }
    }

    /**
     * Gets the UI language.
     *
     * @return the UI language.
     */
    public String getUiLanguage() {
        return uiLanguage;
    }

    /**
     * Sets the UI language.
     *
     * @param code the UI language to set.
     */
    public void setUiLanguage(String code) {
        this.uiLanguage = normalizeUiLanguage(code);
    }

    /**
     * Checks if the UI language is Spanish.
     *
     * @return whether the UI language is Spanish.
     */
    public boolean isSpanish() {
        return "es".equals(uiLanguage);
    }

    /**
     * Normalizes the UI language.
     *
     * @param raw the raw UI language.
     * @return the normalized UI language.
     */
    private static String normalizeUiLanguage(String raw) {
        if (raw == null || raw.isBlank()) {
            return DEFAULT_UI_LANGUAGE;
        }
        String c = raw.trim().toLowerCase();
        if ("es".equals(c) || c.startsWith("es_")) {
            return "es";
        }
        return "en";
    }

    /**
     * Checks if expected fields are visible.
     *
     * @return whether expected fields are visible.
     */
    public boolean isExpectedFieldsVisible() {
        return expectedFieldsVisible;
    }

    /**
     * Sets whether expected fields are visible.
     *
     * @param visible whether expected fields are visible.
     */
    public void setExpectedFieldsVisible(boolean visible) {
        this.expectedFieldsVisible = visible;
    }

    /**
     * Gets the expected field entries.
     *
     * @return the expected field entries.
     */
    public List<String> getExpectedFieldEntries() {
        return Collections.unmodifiableList(expectedFieldEntries);
    }

    /**
     * Sets the expected field entries.
     *
     * @param entries the expected field entries to set.
     */
    public void setExpectedFieldEntries(List<String> entries) {
        expectedFieldEntries.clear();
        if (entries == null) {
            return;
        }
        for (String e : entries) {
            if (e != null) {
                String t = e.trim();
                if (!t.isEmpty()) {
                    expectedFieldEntries.add(t);
                }
            }
        }
    }

    /**
     * Gets the expected fields for the validator.
     *
     * @return the expected fields for the validator.
     */
    public String[] expectedFieldsForValidator() {
        return expectedFieldEntries.toArray(new String[0]);
    }

    /**
     * Gets the expected field count.
     *
     * @return the expected field count.
     */
    public int getExpectedFieldCount() {
        return expectedFieldEntries.size();
    }
}
