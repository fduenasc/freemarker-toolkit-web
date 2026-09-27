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
     * Client property for cursor line (kept in sync by the JS component).
     */
    private static final String PROPERTY_CURSOR_LINE = "cursorLine";
    /**
     * Client property for cursor column (kept in sync by the JS component).
     */
    private static final String PROPERTY_CURSOR_COLUMN = "cursorColumn";
    /**
     * Vaadin event-data expression for cursor line.
     */
    private static final String EVENT_DATA_CURSOR_LINE = "element.cursorLine";
    /**
     * Vaadin event-data expression for cursor column.
     */
    private static final String EVENT_DATA_CURSOR_COLUMN = "element.cursorColumn";
    /**
     * Fallback Vaadin event-data expression for cursor line from CustomEvent detail.
     */
    private static final String EVENT_DATA_DETAIL_LINE = "event.detail.line";
    /**
     * Fallback Vaadin event-data expression for cursor column from CustomEvent detail.
     */
    private static final String EVENT_DATA_DETAIL_COLUMN = "event.detail.column";

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
        getElement().setProperty(PROPERTY_CURSOR_LINE, 1);
        getElement().setProperty(PROPERTY_CURSOR_COLUMN, 1);
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
     * Listens for cursor line/column changes inside the editor.
     *
     * @param listener receives line (1-based) and column (1-based).
     */
    public void addCursorPositionListener(CursorPositionListener listener) {
        getElement().addEventListener("cursor-position-changed", event -> {
                    JsonNode data = event.getEventData();
                    listener.onCursorPosition(
                            readInt(data, EVENT_DATA_CURSOR_LINE, EVENT_DATA_DETAIL_LINE, PROPERTY_CURSOR_LINE, "line"),
                            readInt(data, EVENT_DATA_CURSOR_COLUMN, EVENT_DATA_DETAIL_COLUMN, PROPERTY_CURSOR_COLUMN, "column"));
                }).addEventData(EVENT_DATA_CURSOR_LINE)
                .addEventData(EVENT_DATA_CURSOR_COLUMN)
                .addEventData(EVENT_DATA_DETAIL_LINE)
                .addEventData(EVENT_DATA_DETAIL_COLUMN);
    }

    /**
     * Listener for Monaco cursor position updates.
     */
    @FunctionalInterface
    public interface CursorPositionListener {
        /**
         * Called when the cursor moves.
         *
         * @param line   1-based line number.
         * @param column 1-based column number.
         */
        void onCursorPosition(int line, int column);
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

    private static int readInt(JsonNode data, String flatKey, String detailKey, String shortKey,
                               String detailShortKey) {
        if (data == null) {
            return 1;
        }
        for (String key : new String[]{flatKey, detailKey, shortKey, detailShortKey}) {
            JsonNode node = data.get(key);
            if (isUsableNumber(node)) {
                return toInt(node);
            }
        }
        JsonNode nestedDetail = data.path("event").path("detail").path(detailShortKey);
        if (isUsableNumber(nestedDetail)) {
            return toInt(nestedDetail);
        }
        JsonNode nestedElement = data.path("element").path(shortKey);
        if (isUsableNumber(nestedElement)) {
            return toInt(nestedElement);
        }
        return 1;
    }

    private static boolean isUsableNumber(JsonNode node) {
        return node != null && !node.isNull() && !node.isMissingNode()
                && (node.isNumber() || node.isString());
    }

    private static int toInt(JsonNode node) {
        try {
            if (node.isNumber()) {
                return node.intValue();
            }
            if (node.isString()) {
                String text = node.asString("").trim();
                if (!text.isEmpty()) {
                    return Integer.parseInt(text);
                }
            }
            return node.asInt(1);
        } catch (RuntimeException ignored) {
            return 1;
        }
    }
}
