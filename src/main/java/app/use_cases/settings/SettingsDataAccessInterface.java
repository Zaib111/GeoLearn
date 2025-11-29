package app.use_cases.settings;

/**
 * Interface for accessing and persisting user settings data.
 */
public interface SettingsDataAccessInterface {
    /**
     * Gets the current user settings.
     *
     * @return the current user settings
     */
    UserSettingsData getSettings();

    /**
     * Saves the user settings.
     *
     * @param userSettingsDto the user settings to save
     */
    void saveSettings(UserSettingsData userSettingsDto);
}
