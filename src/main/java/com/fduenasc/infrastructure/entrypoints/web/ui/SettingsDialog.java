package com.fduenasc.infrastructure.entrypoints.web.ui;

import com.fduenasc.infrastructure.entrypoints.web.i18n.Messages;
import com.fduenasc.infrastructure.helpers.UserPreferences;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;

/**
 * A dialog for editing user settings.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public class SettingsDialog extends Dialog {

    /**
     * Constructs a new SettingsDialog instance.
     *
     * @param messages the messages.
     * @param preferences the user preferences.
     * @param onSaved the on saved callback.
     */
    public SettingsDialog(Messages messages, UserPreferences preferences, Runnable onSaved) {
        setHeaderTitle(messages.settingsTitle());
        setWidth("420px");

        ComboBox<String> locale = new ComboBox<>(messages.settingsLocale());
        locale.setItems("en_US", "es_ES", "es_CO", "en_GB");
        locale.setValue(preferences.getLocale());
        locale.setWidthFull();

        ComboBox<String> timeZone = new ComboBox<>(messages.settingsTimeZone());
        timeZone.setItems("UTC", "America/Bogota", "America/New_York", "Europe/Madrid");
        timeZone.setValue(preferences.getTimeZone());
        timeZone.setWidthFull();

        ComboBox<String> language = new ComboBox<>(messages.settingsLanguage());
        language.setItems("es", "en");
        language.setItemLabelGenerator(code -> "es".equals(code) ? "Español" : "English");
        language.setValue(preferences.getUiLanguage());
        language.setWidthFull();

        FormLayout form = new FormLayout(locale, timeZone, language);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(form);

        Button cancel = new Button(messages.cancel(), e -> close());
        Button save = new Button(messages.save(), e -> {
            preferences.setLocale(locale.getValue());
            preferences.setTimeZone(timeZone.getValue());
            preferences.setUiLanguage(language.getValue());
            if (onSaved != null) {
                onSaved.run();
            }
            Notification.show(messages.save(), 2000, Notification.Position.BOTTOM_START);
            close();
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }
}
