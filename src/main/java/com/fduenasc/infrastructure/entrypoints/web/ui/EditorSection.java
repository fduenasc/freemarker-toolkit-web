package com.fduenasc.infrastructure.entrypoints.web.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.function.Consumer;

/**
 * A section for editing text with a title, editor, status label, and toolbar.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class EditorSection extends VerticalLayout {

    /**
     * The title label.
     */
    private final Span titleLabel = new Span();
    /**
     * The editor.
     */
    private final TextArea editor = new TextArea();
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

        editor.setWidthFull();
        editor.setHeight("100%");
        editor.setValueChangeMode(ValueChangeMode.LAZY);
        editor.setValueChangeTimeout(450);
        editor.addClassName("editor-textarea");

        statusLabel.addClassName("editor-status");
        statusLabel.getStyle().set("font-size", "var(--lumo-font-size-s)");

        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        toolbar.addClassName("editor-toolbar");

        HorizontalLayout footer = new HorizontalLayout(statusLabel);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        footer.addClassName("editor-footer");

        VerticalLayout editorWrapper = new VerticalLayout(editor, footer);
        editorWrapper.setSizeFull();
        editorWrapper.setPadding(false);
        editorWrapper.setSpacing(false);
        editorWrapper.expand(editor);

        add(titleLabel, editorWrapper, toolbar);
        expand(editorWrapper);
    }

    /**
     * Gets the editor component.
     *
     * @return the editor component.
     */
    public TextArea getEditor() {
        return editor;
    }

    /**
     * Sets the title of the section.
     *
     * @param title the title of the section.
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
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
        editor.setValue(text != null ? text : "");
    }

    /**
     * Adds a text change listener to the editor.
     *
     * @param handler the text change handler.
     */
    public void onTextChange(Consumer<String> handler) {
        editor.addValueChangeListener(e -> handler.accept(e.getValue()));
    }

    /**
     * Adds an action button to the toolbar.
     *
     * @param label  the label of the button.
     * @param action the action to perform when the button is clicked.
     */
    public void addAction(String label, Runnable action) {
        addAction(label, action, ButtonVariant.LUMO_TERTIARY);
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
        button.addThemeVariants(variant);
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
    }
}
