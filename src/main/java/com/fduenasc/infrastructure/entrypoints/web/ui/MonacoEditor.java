package com.fduenasc.infrastructure.entrypoints.web.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;
import tools.jackson.databind.JsonNode;

import java.util.function.Consumer;

/**
 * Monaco Editor field for template, JSON, and plain text.
 *
 * @author Francisco Dueñas
 * @since 0.3.0
 */
@Tag("monaco-code-editor")
@JsModule("./monaco-code-editor.js")
@NpmPackage(value = "monaco-editor", version = "0.56.0")
public class MonacoEditor extends Component implements HasSize {

    /**
     * FreeMarker 2 syntax (angle tags and dollar interpolation).
     */
    public static final String LANGUAGE_FREEMARKER = "freemarker2";
    /**
     * JSON syntax.
     */
    public static final String LANGUAGE_JSON = "json";
    /**
     * XML syntax.
     */
    public static final String LANGUAGE_XML = "xml";
    /**
     * HTML syntax.
     */
    public static final String LANGUAGE_HTML = "html";
    /**
     * Plain text.
     */
    public static final String LANGUAGE_PLAINTEXT = "plaintext";

    /**
     * Client property and JSON path for the editor text.
     */
    private static final String PROPERTY_VALUE = "value";
    /**
     * Vaadin event-data expression for the editor text.
     */
    private static final String EVENT_DATA_ELEMENT_VALUE = "element.value";

    /**
     * The current editor language id.
     */
    private String language = LANGUAGE_PLAINTEXT;
    /**
     * The current text, including edits not yet pushed as a property round-trip.
     */
    private String value = "";

    /**
     * Constructs an empty editor.
     */
    public MonacoEditor() {
        getElement().setProperty(PROPERTY_VALUE, "");
        getElement().setProperty("language", LANGUAGE_PLAINTEXT);
        getElement().setProperty("wordWrap", true);
        setSizeFull();
        addClassName("monaco-editor-host");
    }

    /**
     * Gets the editor text.
     *
     * @return the editor text.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the editor text.
     *
     * @param value the editor text.
     */
    public void setValue(String value) {
        this.value = value == null ? "" : value;
        getElement().setProperty(PROPERTY_VALUE, this.value);
    }

    /**
     * Gets the Monaco language id.
     *
     * @return the language id.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the Monaco language id.
     *
     * @param language the language id.
     */
    public void setLanguage(String language) {
        this.language = language == null || language.isBlank()
                ? LANGUAGE_PLAINTEXT
                : language;
        getElement().setProperty("language", this.language);
    }

    /**
     * Sets whether the editor accepts typing.
     *
     * @param readOnly {@code true} to block edits.
     */
    public void setReadOnly(boolean readOnly) {
        getElement().setProperty("readOnly", readOnly);
    }

    /**
     * Sets whether long lines wrap inside the editor.
     *
     * @param wordWrap {@code true} to wrap lines.
     */
    public void setWordWrap(boolean wordWrap) {
        getElement().setProperty("wordWrap", wordWrap);
    }

    /**
     * Sets the accessible name announced by the editor.
     *
     * @param label the accessible name.
     */
    public void setLabel(String label) {
        getElement().setProperty("label", label == null ? "" : label);
    }

    /**
     * Listens for text edits. The client sends the value after a short pause,
     * on blur, and before a toolbar button press.
     *
     * @param listener the listener.
     * @return the registration used to remove the listener.
     */
    public Registration addValueChangeListener(Consumer<String> listener) {
        return getElement().addEventListener("value-changed", event -> {
            value = readClientValue(event.getEventData());
            listener.accept(value);
        }).addEventData(EVENT_DATA_ELEMENT_VALUE);
    }

    /**
     * Listens for Monaco Format Document requests on FreeMarker content.
     *
     * @param listener receives the current editor text to format.
     * @return the registration used to remove the listener.
     */
    public Registration addFormatRequestListener(Consumer<String> listener) {
        return getElement().addEventListener("format-request", event -> {
            value = readClientValue(event.getEventData());
            listener.accept(value);
        }).addEventData(EVENT_DATA_ELEMENT_VALUE);
    }

    /**
     * Completes a pending Format Document request with formatted text.
     *
     * @param formatted the formatted FreeMarker template.
     */
    public void completeFormat(String formatted) {
        getElement().callJsFunction("completeFormat", formatted == null ? "" : formatted);
    }

    /**
     * Reads the editor text from a Vaadin DOM event payload.
     *
     * @param data the event data.
     * @return the editor text.
     */
    private static String readClientValue(JsonNode data) {
        if (data == null) {
            return "";
        }
        JsonNode direct = data.get(EVENT_DATA_ELEMENT_VALUE);
        if (direct != null && !direct.isNull()) {
            return direct.asString("");
        }
        JsonNode nested = data.path("element").path(PROPERTY_VALUE);
        if (!nested.isMissingNode() && !nested.isNull()) {
            return nested.asString("");
        }
        return "";
    }
}
