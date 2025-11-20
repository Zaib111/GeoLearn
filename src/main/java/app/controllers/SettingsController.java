package app.controllers;

import app.use_cases.settings.SettingsInputBoundary;
import app.use_cases.settings.UserSettingsData;

public class SettingsController {
    private final SettingsInputBoundary settingsInputBoundary;

    public SettingsController(SettingsInputBoundary settingsInteractor) {
        this.settingsInputBoundary = settingsInteractor;
    }

    public void fetchSettings() {
        settingsInputBoundary.getSettings();
    }

    public void changeUserSettings(UserSettingsData newSettings) {
        settingsInputBoundary.updateUserSettings(newSettings);
    }
}
