package com.fduenasc.infrastructure.entrypoints.web.ui;

import com.fduenasc.infrastructure.entrypoints.web.i18n.Messages;
import com.fduenasc.infrastructure.helpers.UserPreferences;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.List;

/**
 * A dialog for editing expected fields.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class ExpectedFieldsDialog extends Dialog {

    /**
     * A field entry.
     */
    public static class FieldEntry {
        private String path;
        private String type;

        /**
         * Constructs a new FieldEntry instance.
         *
         * @param path the path of the field.
         * @param type the type of the field.
         */
        public FieldEntry(String path, String type) {
            this.path = path;
            this.type = type;
        }

        /**
         * Gets the path of the field.
         *
         * @return the path of the field.
         */
        public String getPath() {
            return path;
        }

        /**
         * Sets the path of the field.
         *
         * @param path the path of the field.
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * Gets the type of the field.
         *
         * @return the type of the field.
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the type of the field.
         *
         * @param type the type of the field.
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Converts the field entry to a string.
         *
         * @return the string representation of the field entry.
         */
        String toEntryString() {
            if (type == null || type.isBlank()) {
                return path;
            }
            return path + ":" + type.toLowerCase();
        }
    }

    /**
     * The messages.
     */
    private final transient Messages messages;
    /**
     * The user preferences.
     */
    private final UserPreferences preferences;
    /**
     * The on saved callback.
     */
    private final transient Runnable onSaved;
    /**
     * The entries.
     */
    private final List<FieldEntry> entries = new ArrayList<>();
    /**
     * The grid.
     */
    private final Grid<FieldEntry> grid = new Grid<>(FieldEntry.class, false);
    /**
     * The editor.
     */
    private final Editor<FieldEntry> editor;

    /**
     * Constructs a new ExpectedFieldsDialog instance.
     *
     * @param messages the messages.
     * @param preferences the user preferences.
     * @param onSaved the on saved callback.
     */
    public ExpectedFieldsDialog(Messages messages, UserPreferences preferences, Runnable onSaved) {
        this.messages = messages;
        this.preferences = preferences;
        this.onSaved = onSaved;

        setHeaderTitle(messages.expectedFieldsDialogTitle());
        setWidth("700px");

        for (String entry : preferences.getExpectedFieldEntries()) {
            String[] parts = entry.split(":", 2);
            entries.add(new FieldEntry(parts[0].trim(), parts.length > 1 ? parts[1].trim() : ""));
        }

        grid.setItems(entries);
        grid.addColumn(FieldEntry::getPath).setHeader(messages.expectedFieldsPath()).setFlexGrow(2);
        grid.addColumn(FieldEntry::getType).setHeader(messages.expectedFieldsType()).setFlexGrow(1);

        TextField pathField = new TextField();
        TextField typeField = new TextField();
        Binder<FieldEntry> binder = new Binder<>(FieldEntry.class);
        binder.forField(pathField).bind(FieldEntry::getPath, FieldEntry::setPath);
        binder.forField(typeField).bind(FieldEntry::getType, FieldEntry::setType);

        editor = grid.getEditor();
        editor.setBinder(binder);
        grid.getColumns().get(0).setEditorComponent(pathField);
        grid.getColumns().get(1).setEditorComponent(typeField);

        grid.addItemDoubleClickListener(e -> {
            editor.editItem(e.getItem());
            pathField.focus();
        });

        Button addRow = new Button(messages.expectedFieldsAddRow(), e -> {
            FieldEntry row = new FieldEntry("", "");
            entries.add(row);
            grid.getDataProvider().refreshAll();
            editor.editItem(row);
            pathField.focus();
        });

        Button deleteRow = new Button(messages.expectedFieldsDeleteRow(), e -> {
            FieldEntry selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                entries.remove(selected);
                grid.getDataProvider().refreshAll();
            }
        });

        HorizontalLayout tableActions = new HorizontalLayout(addRow, deleteRow);

        TextArea importArea = new TextArea();
        importArea.setWidthFull();
        importArea.setHeight("150px");
        importArea.setPlaceholder(messages.expectedFieldsImportHelp());

        Button loadFromText = new Button(messages.expectedFieldsLoadFromText(), e -> applyImportText(importArea.getValue()));

        VerticalLayout importTab = new VerticalLayout(importArea, loadFromText);
        importTab.setPadding(false);

        VerticalLayout content = new VerticalLayout(tableActions, grid, importTab);
        content.setSizeFull();
        content.setPadding(false);
        add(content);

        Button save = new Button(messages.expectedFieldsSave(), e -> saveAndClose());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button close = new Button(messages.expectedFieldsClose(), e -> close());
        getFooter().add(close, save);
    }

    /**
     * Applies the import text.
     *
     * @param raw the raw text.
     */
    private void applyImportText(String raw) {
        entries.clear();
        if (raw != null && !raw.isBlank()) {
            for (String line : raw.lines().toList()) {
                for (String token : splitImportTokens(line)) {
                    String[] p = token.split(":", 2);
                    String path = p[0].trim();
                    String type = p.length > 1 ? p[1].trim() : "";
                    if (!path.isEmpty()) {
                        entries.add(new FieldEntry(path, type));
                    }
                }
            }
        }
        grid.getDataProvider().refreshAll();
    }

    /**
     * Splits the import tokens.
     *
     * @param line the line.
     * @return the list of tokens.
     */
    private static List<String> splitImportTokens(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ',' || Character.isWhitespace(c)) {
                addTokenIfPresent(tokens, current);
            } else {
                current.append(c);
            }
        }
        addTokenIfPresent(tokens, current);
        return tokens;
    }

    /**
     * Adds a token if present.
     *
     * @param tokens the list of tokens.
     * @param current the current token.
     */
    private static void addTokenIfPresent(List<String> tokens, StringBuilder current) {
        if (!current.isEmpty()) {
            tokens.add(current.toString());
            current.setLength(0);
        }
    }

    /**
     * Saves and closes the dialog.
     */
    private void saveAndClose() {
        editor.cancel();
        List<String> saved = entries.stream()
                .filter(e -> e.getPath() != null && !e.getPath().isBlank())
                .map(FieldEntry::toEntryString)
                .toList();
        preferences.setExpectedFieldEntries(saved);
        if (onSaved != null) {
            onSaved.run();
        }
        Notification.show(messages.save(), 2000, Notification.Position.BOTTOM_START);
        close();
    }
}
