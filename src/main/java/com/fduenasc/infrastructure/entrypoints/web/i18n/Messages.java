package com.fduenasc.infrastructure.entrypoints.web.i18n;

import com.fduenasc.domain.model.MessageKeys;
import com.fduenasc.infrastructure.helpers.UserPreferences;

/**
 * Provides messages for the application UI.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public final class Messages {

    private final UserPreferences preferences;

    /**
     * Constructs a new Messages instance.
     *
     * @param preferences the user preferences.
     */
    public Messages(UserPreferences preferences) {
        this.preferences = preferences;
    }

    /**
     * Checks if the UI language is Spanish.
     *
     * @return whether the UI language is Spanish.
     */
    private boolean es() {
        return preferences.isSpanish();
    }

    /**
     * Gets the window title.
     *
     * @return the window title.
     */
    public String windowTitle() {
        return es()
                ? "Kit FreeMarker JSON/XML (Apache FreeMarker 2.3.34)"
                : "FreeMarker JSON/XML Toolkit (Apache FreeMarker 2.3.34)";
    }

    /**
     * Gets the template panel title.
     *
     * @return the template panel title.
     */
    public String panelTemplate() {
        return es() ? "Plantilla" : "Template";
    }

    /**
     * Gets the data model panel title.
     *
     * @return the data model panel title.
     */
    public String panelDataModel() {
        return es() ? "Modelo de datos" : "Data Model";
    }

    /**
     * Gets the rendered result panel title.
     *
     * @return the rendered result panel title.
     */
    public String panelRenderedResult() {
        return es() ? "Resultado renderizado" : "Rendered Result";
    }

    /**
     * Gets the expected fields panel title.
     *
     * @return the expected fields panel title.
     */
    public String panelExpectedFields() {
        return es() ? "Campos esperados" : "Expected fields";
    }

    /**
     * Gets the process template button text.
     *
     * @return the process template button text.
     */
    public String processTemplate() {
        return es() ? "Procesar plantilla" : "Process template";
    }

    /**
     * Gets the format JSON button text.
     *
     * @return the format JSON button text.
     */
    public String formatJson() {
        return es() ? "Formatear JSON" : "Format JSON";
    }

    /**
     * Gets the format template button text.
     *
     * @return the format template button text.
     */
    public String formatTemplate() {
        return es() ? "Formatear plantilla" : "Format template";
    }

    /**
     * Gets the single line button text.
     *
     * @return the single line button text.
     */
    public String singleLine() {
        return es() ? "Una línea" : "Single line";
    }

    /**
     * Gets the clear output button text.
     *
     * @return the clear output button text.
     */
    public String clearOutput() {
        return es() ? "Limpiar" : "Clear";
    }

    /**
     * Gets the configure button text.
     *
     * @return the configure button text.
     */
    public String configure() {
        return es() ? "Configurar" : "Configure";
    }

    /**
     * Gets the validate fields button text.
     *
     * @return the validate fields button text.
     */
    public String validateFields() {
        return es() ? "Validar campos" : "Validate fields";
    }

    /**
     * Gets the settings button text.
     *
     * @return the settings button text.
     */
    public String settings() {
        return es() ? "Configuración" : "Settings";
    }

    /**
     * Gets the show expected fields button text.
     *
     * @return the show expected fields button text.
     */
    public String showExpectedFields() {
        return es() ? "Mostrar campos esperados" : "Show expected fields";
    }

    /**
     * Gets the invalid JSON button text.
     *
     * @return the invalid JSON button text.
     */
    public String invalidJson() {
        return es() ? "JSON inválido" : "Invalid JSON";
    }

    /**
     * Gets the valid JSON button text.
     *
     * @return the valid JSON button text.
     */
    public String validJson() {
        return es() ? "JSON válido" : "Valid JSON";
    }

    /**
     * Gets the invalid template button text.
     *
     * @return the invalid template button text.
     */
    public String invalidTemplate() {
        return es() ? "Plantilla inválida" : "Invalid template";
    }

    /**
     * Gets the template OK button text.
     *
     * @return the template OK button text.
     */
    public String templateOk() {
        return es() ? "Plantilla OK" : "Template OK";
    }

    /**
     * Gets the line button text.
     *
     * @return the line button text.
     */
    public String line() {
        return es() ? "línea" : "line";
    }

    /**
     * Gets the column button text.
     *
     * @return the column button text.
     */
    public String column() {
        return es() ? "columna" : "column";
    }

    /**
     * Gets the error processing template button text.
     *
     * @return the error processing template button text.
     */
    public String errorProcessingTemplate() {
        return es() ? "Error al procesar la plantilla: " : "Error processing template: ";
    }

    /**
     * Gets the template syntax error button text.
     *
     * @return the template syntax error button text.
     */
    public String templateSyntaxError() {
        return es() ? "Error de sintaxis en la plantilla" : "Template syntax error";
    }

    /**
     * Gets the no-expected fields button text.
     *
     * @return the no expected fields button text.
     */
    public String noExpectedFields() {
        return es() ? "No hay campos esperados configurados" : "No expected fields configured";
    }

    /**
     * Gets the all-field present button text.
     *
     * @return the all fields present button text.
     */
    public String allFieldsPresent() {
        return es() ? "Todos los campos esperados están presentes" : "All expected fields are present";
    }

    /**
     * Gets the missing fields prefix button text.
     *
     * @return the missing fields prefix button text.
     */
    public String missingFieldsPrefix() {
        return es() ? "Campos faltantes: " : "Missing fields: ";
    }

    /**
     * Gets the invalid JSON output button text.
     *
     * @return the invalid JSON output button text.
     */
    public String invalidJsonOutput() {
        return es() ? "La salida no es JSON válido" : "Output is not valid JSON";
    }

    /**
     * Gets the expected fields summary none button text.
     *
     * @return the expected fields summary none button text.
     */
    public String expectedFieldsSummaryNone() {
        return es() ? "(ninguno configurado)" : "(none configured)";
    }

    /**
     * Gets the expected fields summary one button text.
     *
     * @return the expected fields summary one button text.
     */
    public String expectedFieldsSummaryOne() {
        return es() ? "(1 campo)" : "(1 field)";
    }

    /**
     * Gets the expected fields summary many button text.
     *
     * @param n the number of fields.
     * @return the expected fields summary many button text.
     */
    public String expectedFieldsSummaryMany(int n) {
        return es() ? "(" + n + " campos)" : "(" + n + " fields)";
    }

    /**
     * Resolves the data model message.
     *
     * @param key the key of the message.
     * @return the resolved data model message.
     */
    public String resolveDataModelMessage(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        return switch (key) {
            case MessageKeys.JSON_DATA_MODEL_NULL_ROOT ->
                    es() ? "La raíz null es válida; FreeMarker verá un valor null." : "Null root is valid; FreeMarker will see a null value.";
            case MessageKeys.JSON_DATA_MODEL_NEED_OBJECT ->
                    es() ? "Se recomienda un objeto JSON como raíz del modelo de datos." : "A JSON object root is recommended for the data model.";
            case MessageKeys.JSON_PARSE_FALLBACK -> es() ? "Error de análisis JSON" : "JSON parse error";
            default -> key;
        };
    }

    /**
     * Gets the format JSON error button text.
     *
     * @return the format JSON error button text.
     */
    public String formatJsonError() {
        return es() ? "Error al formatear JSON" : "JSON format error";
    }

    /**
     * Gets the expected fields dialog title button text.
     *
     * @return the expected fields dialog title button text.
     */
    public String expectedFieldsDialogTitle() {
        return es() ? "Campos esperados en la salida" : "Expected output fields";
    }

    /**
     * Gets the expected fields save button text.
     *
     * @return the expected fields save button text.
     */
    public String expectedFieldsSave() {
        return es() ? "Guardar" : "Save";
    }

    /**
     * Gets the expected fields close button text.
     *
     * @return the expected fields close button text.
     */
    public String expectedFieldsClose() {
        return es() ? "Cerrar" : "Close";
    }

    /**
     * Gets the expected fields path button text.
     *
     * @return the expected fields path button text.
     */
    public String expectedFieldsPath() {
        return es() ? "Ruta" : "Path";
    }

    /**
     * Gets the expected fields type button text.
     *
     * @return the expected fields type button text.
     */
    public String expectedFieldsType() {
        return es() ? "Tipo (opcional)" : "Type (optional)";
    }

    /**
     * Gets the expected fields add row button text.
     *
     * @return the expected fields add row button text.
     */
    public String expectedFieldsAddRow() {
        return es() ? "Añadir fila" : "Add row";
    }

    /**
     * Gets the expected fields delete row button text.
     *
     * @return the expected fields delete row button text.
     */
    public String expectedFieldsDeleteRow() {
        return es() ? "Eliminar fila" : "Delete row";
    }

    /**
     * Gets the expected fields import help button text.
     *
     * @return the expected fields import help button text.
     */
    public String expectedFieldsImportHelp() {
        return es()
                ? "Pega rutas separadas por comas, espacios o saltos de línea.\nEjemplo: name:string, address.city, items:array"
                : "Paste paths separated by commas, spaces, or newlines.\nExample: name:string, address.city, items:array";
    }

    /**
     * Gets the expected fields load from the text button text.
     *
     * @return the expected fields load from the text button text.
     */
    public String expectedFieldsLoadFromText() {
        return es() ? "Cargar desde texto" : "Load from text";
    }

    /**
     * Gets the settings title button text.
     *
     * @return the settings title button text.
     */
    public String settingsTitle() {
        return es() ? "Configuración" : "Settings";
    }

    /**
     * Gets the settings locale button text.
     *
     * @return the settings locale button text.
     */
    public String settingsLocale() {
        return es() ? "Locale FreeMarker" : "FreeMarker locale";
    }

    /**
     * Gets the settings time zone button text.
     *
     * @return the settings time zone button text.
     */
    public String settingsTimeZone() {
        return es() ? "Zona horaria" : "Time zone";
    }

    /**
     * Gets the settings language button text.
     *
     * @return the settings language button text.
     */
    public String settingsLanguage() {
        return es() ? "Idioma de la interfaz" : "UI language";
    }

    /**
     * Gets the save button text.
     *
     * @return the save button text.
     */
    public String save() {
        return es() ? "Guardar" : "Save";
    }

    /**
     * Gets the cancel button text.
     *
     * @return the cancel button text.
     */
    public String cancel() {
        return es() ? "Cancelar" : "Cancel";
    }
}
