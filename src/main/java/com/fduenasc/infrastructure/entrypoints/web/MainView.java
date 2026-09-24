package com.fduenasc.infrastructure.entrypoints.web;

import com.fduenasc.application.ToolkitService;
import com.fduenasc.domain.model.FreemarkerTemplateSyntaxCheck;
import com.fduenasc.domain.model.JsonSyntaxCheck;
import com.fduenasc.domain.usecase.exception.DataModelException;
import com.fduenasc.domain.usecase.exception.InvalidJsonException;
import com.fduenasc.domain.usecase.exception.TemplateProcessingException;
import com.fduenasc.infrastructure.entrypoints.web.i18n.Messages;
import com.fduenasc.infrastructure.entrypoints.web.ui.EditorSection;
import com.fduenasc.infrastructure.entrypoints.web.ui.MonacoEditor;
import com.fduenasc.infrastructure.entrypoints.web.ui.ExpectedFieldsDialog;
import com.fduenasc.infrastructure.entrypoints.web.ui.SettingsDialog;
import com.fduenasc.infrastructure.helpers.UserPreferences;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import java.util.List;

/**
 * The main view of the application.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
@Route("")
@PageTitle("FreeMarker JSON/XML Toolkit")
public class MainView extends VerticalLayout {

    /**
     * The style color.
     */
    private static final String STYLE_COLOR = "color";
    /**
     * The Lumo success color.
     */
    private static final String LUMO_SUCCESS_COLOR = "var(--lumo-success-color)";
    /**
     * The Lumo error color.
     */
    private static final String LUMO_ERROR_COLOR = "var(--lumo-error-color)";

    /**
     * The toolkit service.
     */
    private final ToolkitService toolkitService;
    /**
     * The user preferences.
     */
    private final UserPreferences preferences;

    /**
     * The messages.
     */
    private transient Messages messages;
    /**
     * The compact brand title in the header.
     */
    private Span brandTitle;
    /**
     * The template panel.
     */
    private EditorSection templatePanel;
    /**
     * The data panel.
     */
    private EditorSection dataPanel;
    /**
     * The output panel.
     */
    private EditorSection outputPanel;
    /**
     * The expected fields summary.
     */
    private Span expectedFieldsSummary;
    /**
     * The validation result.
     */
    private Span validationResult;
    /**
     * The expected fields footer.
     */
    private VerticalLayout expectedFieldsFooter;

    /**
     * Constructs a new MainView instance.
     *
     * @param toolkitService the toolkit service.
     * @param preferences the user preferences.
     */
    @Inject
    public MainView(ToolkitService toolkitService, UserPreferences preferences) {
        this.toolkitService = toolkitService;
        this.preferences = preferences;
    }

    /**
     * Initializes the main view.
     */
    @PostConstruct
    void init() {
        messages = new Messages(preferences);
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("toolkit-main");

        UI.getCurrent().getPage().setTitle(messages.windowTitle());

        buildLayout();
        refreshAllChrome();
    }

    /**
     * Builds the layout.
     */
    private void buildLayout() {
        removeAll();

        brandTitle = new Span(Messages.APP_TITLE);
        brandTitle.addClassName("toolkit-brand");

        Span brandMeta = new Span("Apache FreeMarker 2.3.34");
        brandMeta.addClassName("toolkit-brand-meta");

        VerticalLayout brandBlock = new VerticalLayout(brandTitle, brandMeta);
        brandBlock.setPadding(false);
        brandBlock.setSpacing(false);
        brandBlock.addClassName("toolkit-brand-block");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        menuBar.addItem(messages.settings(), e -> openSettings());

        Checkbox showExpectedFields = new Checkbox(messages.showExpectedFields(), preferences.isExpectedFieldsVisible());
        showExpectedFields.addClassName("toolkit-header-check");
        showExpectedFields.addValueChangeListener(e -> {
            preferences.setExpectedFieldsVisible(e.getValue());
            expectedFieldsFooter.setVisible(e.getValue());
        });

        HorizontalLayout headerActions = new HorizontalLayout(showExpectedFields, menuBar);
        headerActions.setAlignItems(FlexComponent.Alignment.CENTER);
        headerActions.setSpacing(true);
        headerActions.setPadding(false);
        headerActions.addClassName("toolkit-header-actions");

        HorizontalLayout header = new HorizontalLayout(brandBlock, headerActions);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(brandBlock);
        header.addClassName("toolkit-header");

        templatePanel = createTemplatePanel();
        templatePanel.addClassName("panel-template");
        dataPanel = createDataPanel();
        dataPanel.addClassName("panel-data");
        outputPanel = createOutputPanel();
        outputPanel.addClassName("panel-output");

        Div workspace = new Div(templatePanel, dataPanel, outputPanel);
        workspace.addClassName("toolkit-workspace");
        workspace.setSizeFull();

        expectedFieldsFooter = buildExpectedFieldsFooter();
        expectedFieldsFooter.setVisible(preferences.isExpectedFieldsVisible());

        add(header, workspace, expectedFieldsFooter);
        expand(workspace);
    }

    /**
     * Creates the template panel.
     *
     * @return the template panel.
     */
    private EditorSection createTemplatePanel() {
        EditorSection panel = new EditorSection(messages.panelTemplate());
        panel.setLanguageOptions(messages.editorLanguage(), templateLanguageChoices(), MonacoEditor.LANGUAGE_FREEMARKER);
        panel.setWordWrapLabel(messages.editorWordWrap());
        panel.setDocumentFormatter(toolkitService::formatTemplate);
        panel.addAction(messages.singleLine(), this::setTemplateSingleLine);
        panel.onTextChange(text -> refreshTemplateStatus());
        return panel;
    }

    /**
     * Creates the data panel.
     *
     * @return the data panel.
     */
    private EditorSection createDataPanel() {
        EditorSection panel = new EditorSection(messages.panelDataModel());
        panel.setLanguageOptions(messages.editorLanguage(), dataLanguageChoices(), MonacoEditor.LANGUAGE_JSON);
        panel.setWordWrapLabel(messages.editorWordWrap());
        panel.onTextChange(text -> refreshJsonStatus());
        return panel;
    }

    /**
     * Creates the output panel.
     *
     * @return the output panel.
     */
    private EditorSection createOutputPanel() {
        EditorSection panel = new EditorSection(messages.panelRenderedResult());
        panel.setLanguageOptions(messages.editorLanguage(), outputLanguageChoices(), MonacoEditor.LANGUAGE_PLAINTEXT);
        panel.setWordWrapLabel(messages.editorWordWrap());
        panel.setReadOnly(true);
        panel.setStatusVisible(false);
        panel.addPrimaryAction(messages.processTemplate(), this::processTemplate);
        panel.addAction(messages.clearOutput(), () -> panel.setText(""));
        return panel;
    }

    /**
     * Builds the language choices for the template panel.
     *
     * @return the template language choices.
     */
    private List<EditorSection.LanguageChoice> templateLanguageChoices() {
        return List.of(
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_FREEMARKER, Messages.LANGUAGE_FREEMARKER),
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_HTML, Messages.LANGUAGE_HTML),
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_PLAINTEXT, messages.languagePlaintext())
        );
    }

    /**
     * Builds the language choices for the data panel.
     *
     * @return the data language choices.
     */
    private List<EditorSection.LanguageChoice> dataLanguageChoices() {
        return List.of(
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_JSON, Messages.LANGUAGE_JSON),
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_XML, Messages.LANGUAGE_XML),
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_PLAINTEXT, messages.languagePlaintext())
        );
    }

    /**
     * Builds the language choices for the output panel.
     *
     * @return the output language choices.
     */
    private List<EditorSection.LanguageChoice> outputLanguageChoices() {
        return List.of(
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_PLAINTEXT, messages.languagePlaintext()),
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_JSON, Messages.LANGUAGE_JSON),
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_XML, Messages.LANGUAGE_XML),
                new EditorSection.LanguageChoice(MonacoEditor.LANGUAGE_HTML, Messages.LANGUAGE_HTML)
        );
    }

    /**
     * Builds the expected fields footer.
     *
     * @return the expected fields footer.
     */
    private VerticalLayout buildExpectedFieldsFooter() {
        Span title = new Span(messages.panelExpectedFields());
        title.addClassName("expected-fields-title");

        expectedFieldsSummary = new Span();
        expectedFieldsSummary.addClassName("expected-fields-summary");

        validationResult = new Span();
        validationResult.addClassName("expected-fields-validation");

        Button configure = new Button(messages.configure(), e ->
                new ExpectedFieldsDialog(messages, preferences, this::refreshExpectedFieldsSummary).open());
        configure.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        Button validate = new Button(messages.validateFields(), e -> validateExpectedFields());
        validate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        HorizontalLayout actions = new HorizontalLayout(configure, validate);
        actions.setSpacing(true);

        HorizontalLayout row = new HorizontalLayout(title, expectedFieldsSummary, validationResult, actions);
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.addClassName("expected-fields-footer");

        VerticalLayout footer = new VerticalLayout(row);
        footer.setPadding(false);
        footer.setSpacing(false);
        footer.setWidthFull();
        footer.addClassName("expected-fields-bar");
        return footer;
    }

    /**
     * Processes the template.
     */
    private void processTemplate() {
        String template = templatePanel.getText();
        FreemarkerTemplateSyntaxCheck syntaxCheck = toolkitService.checkTemplate(template);
        if (!syntaxCheck.syntaxValid()) {
            outputPanel.setText(formatTemplateSyntaxError(syntaxCheck));
            refreshTemplateStatus();
            refreshJsonStatus();
            return;
        }
        try {
            String output = toolkitService.processTemplate(template, dataPanel.getText());
            outputPanel.setText(output);
        } catch (TemplateProcessingException | DataModelException ex) {
            outputPanel.setText(messages.errorProcessingTemplate() + ex.getMessage());
        }
        refreshTemplateStatus();
        refreshJsonStatus();
    }

    /**
     * Formats the template syntax error.
     *
     * @param check the syntax check.
     * @return the formatted template syntax error.
     */
    private String formatTemplateSyntaxError(FreemarkerTemplateSyntaxCheck check) {
        StringBuilder sb = new StringBuilder(messages.templateSyntaxError());
        if (check.line() > 0) {
            sb.append(" (").append(messages.line()).append(' ').append(check.line());
            if (check.column() > 0) {
                sb.append(", ").append(messages.column()).append(' ').append(check.column());
            }
            sb.append(')');
        }
        sb.append(":\n\n");
        if (check.message() != null && !check.message().isBlank()) {
            sb.append(check.message());
        }
        return sb.toString();
    }

    /**
     * Validates the expected fields.
     */
    private void validateExpectedFields() {
        String output = outputPanel.getText();
        String[] fields = preferences.expectedFieldsForValidator();
        if (fields.length == 0) {
            validationResult.setText(messages.noExpectedFields());
            validationResult.getStyle().set(STYLE_COLOR, "var(--lumo-secondary-text-color)");
            return;
        }
        try {
            List<String> missing = toolkitService.validateExpectedFields(output, fields);
            if (missing.isEmpty()) {
                validationResult.setText(messages.allFieldsPresent());
                validationResult.getStyle().set(STYLE_COLOR, LUMO_SUCCESS_COLOR);
            } else {
                validationResult.setText(messages.missingFieldsPrefix() + String.join(", ", missing));
                validationResult.getStyle().set(STYLE_COLOR, LUMO_ERROR_COLOR);
            }
        } catch (InvalidJsonException e) {
            validationResult.setText(messages.invalidJsonOutput());
            validationResult.getStyle().set(STYLE_COLOR, LUMO_ERROR_COLOR);
        }
    }

    /**
     * Sets the template to a single line.
     */
    private void setTemplateSingleLine() {
        templatePanel.setText(toolkitService.toSingleLine(templatePanel.getText()));
        refreshTemplateStatus();
    }

    /**
     * Refreshes the JSON status.
     */
    private void refreshJsonStatus() {
        JsonSyntaxCheck check = toolkitService.checkJson(dataPanel.getText());
        if (!check.syntaxValid()) {
            dataPanel.setStatus(formatStatusWithLocation(messages.invalidJson(), check.line(), check.column()), LUMO_ERROR_COLOR);
            return;
        }
        if (check.message() != null && !check.message().isEmpty()) {
            dataPanel.setStatus(messages.resolveDataModelMessage(check.message()), "var(--lumo-warning-text-color)");
            return;
        }
        dataPanel.setStatus(messages.validJson(), LUMO_SUCCESS_COLOR);
    }

    /**
     * Refreshes the template status.
     */
    private void refreshTemplateStatus() {
        FreemarkerTemplateSyntaxCheck check = toolkitService.checkTemplate(templatePanel.getText());
        if (!check.syntaxValid()) {
            templatePanel.setStatus(formatStatusWithLocation(messages.invalidTemplate(), check.line(), check.column()), LUMO_ERROR_COLOR);
            return;
        }
        templatePanel.setStatus(messages.templateOk(), LUMO_SUCCESS_COLOR);
    }

    /**
     * Formats the status with location.
     *
     * @param prefix the prefix.
     * @param line the line.
     * @param column the column.
     * @return the formatted status with location.
     */
    private static String formatStatusWithLocation(String prefix, int line, int column) {
        if (line <= 0) {
            return prefix;
        }
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(" (L ").append(line);
        if (column > 0) {
            sb.append(", C ").append(column);
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Refreshes the expected fields summary.
     */
    private void refreshExpectedFieldsSummary() {
        int n = preferences.getExpectedFieldCount();
        String text = switch (n) {
            case 0 -> messages.expectedFieldsSummaryNone();
            case 1 -> messages.expectedFieldsSummaryOne();
            default -> messages.expectedFieldsSummaryMany(n);
        };
        expectedFieldsSummary.setText(text);
    }

    /**
     * Opens the settings dialog.
     */
    private void openSettings() {
        new SettingsDialog(messages, preferences, this::refreshAllChrome).open();
    }

    /**
     * Refreshes all chrome.
     */
    private void refreshAllChrome() {
        UI.getCurrent().getPage().setTitle(messages.windowTitle());
        if (brandTitle != null) {
            brandTitle.setText(Messages.APP_TITLE);
        }
        templatePanel.setTitle(messages.panelTemplate());
        dataPanel.setTitle(messages.panelDataModel());
        outputPanel.setTitle(messages.panelRenderedResult());
        templatePanel.refreshLanguageOptions(messages.editorLanguage(), templateLanguageChoices());
        dataPanel.refreshLanguageOptions(messages.editorLanguage(), dataLanguageChoices());
        outputPanel.refreshLanguageOptions(messages.editorLanguage(), outputLanguageChoices());
        templatePanel.setWordWrapLabel(messages.editorWordWrap());
        dataPanel.setWordWrapLabel(messages.editorWordWrap());
        outputPanel.setWordWrapLabel(messages.editorWordWrap());
        refreshJsonStatus();
        refreshTemplateStatus();
        refreshExpectedFieldsSummary();
    }
}
