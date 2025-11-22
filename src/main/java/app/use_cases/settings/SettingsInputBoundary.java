package app.use_cases.settings;

/**
 * Input boundary interface for settings use case.
 */
public interface SettingsInputBoundary {
    /**
     * Retrieves the current user settings.
     */
    void getSettings();

    /**
     * Updates the user settings.
     *
     * @param userSettingsDto the new user settings data to apply
     */
    void updateUserSettings(UserSettingsData userSettingsDto);
}
