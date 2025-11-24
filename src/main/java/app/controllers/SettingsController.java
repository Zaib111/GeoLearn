package app.controllers;

import app.use_cases.settings.SettingsInputBoundary;
import app.use_cases.settings.UserSettingsData;

/**
 * Controller for handling settings-related user interactions.
 */
public class SettingsController {
    private final SettingsInputBoundary settingsInputBoundary;

    /**
     * Constructs a SettingsController.
     *
     * @param settingsInteractor the settings input boundary
     */
    public SettingsController(SettingsInputBoundary settingsInteractor) {
        this.settingsInputBoundary = settingsInteractor;
    }

    /**
     * Fetches the current user settings.
     */
    public void fetchSettings() {
        settingsInputBoundary.getSettings();
    }

    /**
     * Changes the user settings.
     *
     * @param newSettings the new settings to apply
     */
    public void changeUserSettings(UserSettingsData newSettings) {
        settingsInputBoundary.updateUserSettings(newSettings);
    }
}
