package app.use_cases.settings;

/**
 * Output boundary interface for settings use case.
 */
public interface SettingsOutputBoundary {
    /**
     * Handles successful retrieval of settings.
     *
     * @param userSettingsDto the retrieved user settings data
     */
    void getSettingsSuccess(UserSettingsData userSettingsDto);

    /**
     * Handles successful update of user settings.
     *
     * @param userSettingsDto the updated user settings data
     */
    void updateUserSettingsSuccess(UserSettingsData userSettingsDto);

}
