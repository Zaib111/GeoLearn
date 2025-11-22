package app.use_cases.settings;

public interface SettingsDataAccessInterface {
    UserSettingsData getSettings();
    void saveSettings(UserSettingsData userSettingsDto);
}
