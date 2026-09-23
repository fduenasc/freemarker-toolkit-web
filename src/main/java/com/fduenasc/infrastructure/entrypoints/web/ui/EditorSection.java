package com.fduenasc.infrastructure.entrypoints.web.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.function.Consumer;

/**
 * A section for editing text with a title, language selector, editor, status, and toolbar.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class EditorSection extends VerticalLayout {

    /**
     * A selectable Monaco language option.
     *
     * @param id    Monaco language id.
     * @param label display label.
     */
    public record LanguageChoice(String id, String label) {
    }

    /**
     * The title label.
     */
    private final Span titleLabel = new Span();
    /**
     * The language selector label.
     */
    private final Span languageLabel = new Span();
    /**
     * The language selector.
     */
    private final Select<LanguageChoice> languageSelect = new Select<>();
    /**
     * The language selector row.
     */
    private final HorizontalLayout languageRow = new HorizontalLayout();
    /**
     * Word-wrap toggle for this editor.
     */
    private final Checkbox wordWrapToggle = new Checkbox();
    /**
     * The editor.
     */
    private final MonacoEditor editor = new MonacoEditor();
    /**
     * Registration for the text-change listener, if any.
     */
    private Registration textChangeRegistration;
    /**
     * The status label.
     */
    private final Span statusLabel = new Span();
    /**
     * The toolbar.
     */
    private final HorizontalLayout toolbar = new HorizontalLayout();

    /**
     * Constructs a new EditorSection instance.
     *
     * @param title the title of the section.
     */
    public EditorSection(String title) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("editor-section");

        titleLabel.setText(title);
        titleLabel.addClassName("editor-section-title");

        languageLabel.addClassName("editor-language-label");
        languageSelect.setItemLabelGenerator(LanguageChoice::label);
        languageSelect.setWidth("9.5rem");
        languageSelect.addClassName("editor-language-select");
        languageSelect.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                editor.setLanguage(event.getValue().id());
            }
        });

        languageRow.setAlignItems(FlexComponent.Alignment.CENTER);
        languageRow.setSpacing(true);
        languageRow.setPadding(false);
        languageRow.addClassName("editor-language-row");
        languageRow.add(languageLabel, languageSelect);
        languageRow.setVisible(false);

        wordWrapToggle.setValue(true);
        wordWrapToggle.addClassName("editor-wrap-toggle");
        wordWrapToggle.addValueChangeListener(event -> editor.setWordWrap(Boolean.TRUE.equals(event.getValue())));
        editor.setWordWrap(true);

        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setSpacing(true);
        toolbar.setPadding(false);
        toolbar.addClassName("editor-toolbar");

        HorizontalLayout header = new HorizontalLayout(titleLabel, languageRow, wordWrapToggle, toolbar);
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(titleLabel);
        header.addClassName("editor-section-header");

        editor.setWidthFull();
        editor.setHeight("100%");
        editor.setLabel(title);
        editor.addClassName("editor-body");

        statusLabel.addClassName("editor-status");

        HorizontalLayout footer = new HorizontalLayout(statusLabel);
        footer.setWidthFull();
        footer.setPadding(false);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        footer.addClassName("editor-footer");

        VerticalLayout body = new VerticalLayout(editor);
        body.setSizeFull();
        body.setPadding(false);
        body.setSpacing(false);
        body.addClassName("editor-section-body");
        body.expand(editor);

        add(header, body, footer);
        expand(body);
    }

    /**
     * Shows a language selector with the given options.
     *
     * @param label    the selector label.
     * @param choices  the available languages.
     * @param selected the initially selected language id.
     */
    public void setLanguageOptions(String label, List<LanguageChoice> choices, String selected) {
        languageLabel.setText(label);
        languageSelect.setItems(choices);
        languageRow.setVisible(choices != null && !choices.isEmpty());
        selectLanguage(selected);
    }

    /**
     * Updates the language selector label and option labels while keeping the selection.
     *
     * @param label   the selector label.
     * @param choices the available languages with refreshed labels.
     */
    public void refreshLanguageOptions(String label, List<LanguageChoice> choices) {
        String current = editor.getLanguage();
        setLanguageOptions(label, choices, current);
    }

    /**
     * Sets the word-wrap checkbox label.
     *
     * @param label the wrap label.
     */
    public void setWordWrapLabel(String label) {
        wordWrapToggle.setLabel(label);
    }

    /**
     * Sets whether long lines wrap inside the editor.
     *
     * @param wordWrap {@code true} to wrap lines.
     */
    public void setWordWrap(boolean wordWrap) {
        wordWrapToggle.setValue(wordWrap);
        editor.setWordWrap(wordWrap);
    }

    /**
     * Sets the Monaco language id and syncs the selector when present.
     *
     * @param language the language id.
     */
    public void setLanguage(String language) {
        editor.setLanguage(language);
        selectLanguage(language);
    }

    /**
     * Gets the Monaco language id.
     *
     * @return the language id.
     */
    public String getLanguage() {
        return editor.getLanguage();
    }

    /**
     * Sets the minimum height of the editor.
     *
     * @param minHeight the CSS min-height.
     */
    public void setEditorMinHeight(String minHeight) {
        editor.setMinHeight(minHeight);
    }

    /**
     * Sets the title of the section.
     *
     * @param title the title of the section.
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
        editor.setLabel(title);
    }

    /**
     * Sets the read-only state of the editor.
     *
     * @param readOnly the read-only state of the editor.
     */
    public void setReadOnly(boolean readOnly) {
        editor.setReadOnly(readOnly);
    }

    /**
     * Gets the text of the editor.
     *
     * @return the text of the editor.
     */
    public String getText() {
        return editor.getValue() != null ? editor.getValue() : "";
    }

    /**
     * Sets the text of the editor.
     *
     * @param text the text of the editor.
     */
    public void setText(String text) {
        editor.setValue(text);
    }

    /**
     * Adds a text change listener to the editor.
     *
     * @param handler the text change handler.
     */
    public void onTextChange(Consumer<String> handler) {
        if (textChangeRegistration != null) {
            textChangeRegistration.remove();
        }
        textChangeRegistration = editor.addValueChangeListener(handler);
    }

    /**
     * Adds an action button to the toolbar.
     *
     * @param label  the label of the button.
     * @param action the action to perform when the button is clicked.
     */
    public void addAction(String label, Runnable action) {
        addAction(label, action, ButtonVariant.LUMO_TERTIARY_INLINE);
    }

    /**
     * Adds a primary action button to the toolbar.
     *
     * @param label  the label of the button.
     * @param action the action to perform when the button is clicked.
     */
    public void addPrimaryAction(String label, Runnable action) {
        addAction(label, action, ButtonVariant.LUMO_PRIMARY);
    }

    /**
     * Adds an action button to the toolbar.
     *
     * @param label   the label of the button.
     * @param action  the action to perform when the button is clicked.
     * @param variant the variant of the button.
     */
    private void addAction(String label, Runnable action, ButtonVariant variant) {
        Button button = new Button(label, e -> action.run());
        button.addThemeVariants(variant, ButtonVariant.LUMO_SMALL);
        toolbar.add(button);
    }

    /**
     * Sets the status of the section.
     *
     * @param text  the text of the status.
     * @param color the color of the status.
     */
    public void setStatus(String text, String color) {
        statusLabel.setText(text != null ? text : "");
        if (color != null) {
            statusLabel.getStyle().set("color", color);
        }
    }

    /**
     * Sets the visibility of the status label.
     *
     * @param visible the visibility of the status label.
     */
    public void setStatusVisible(boolean visible) {
        statusLabel.setVisible(visible);
        statusLabel.getParent().ifPresent(parent -> parent.setVisible(visible));
    }

    /**
     * Selects the language option matching the given Monaco id.
     *
     * @param languageId the language id.
     */
    private void selectLanguage(String languageId) {
        if (!languageRow.isVisible()) {
            return;
        }
        languageSelect.getListDataView().getItems()
                .filter(choice -> choice.id().equals(languageId))
                .findFirst()
                .ifPresentOrElse(
                        languageSelect::setValue,
                        () -> languageSelect.getListDataView().getItems().findFirst()
                                .ifPresent(languageSelect::setValue));
    }
}
